# МОДЕЛЬ ПРЕДМЕТНОЙ ОБЛАСТИ — Lab1 (вариант 342951)

## 1. Текст предметной области (источник)
"Бомбардировка возобновилась... Они сгрудились плотнее и стали ждать конца."

## 2. Ключевые сущности

### 2.1 Bombardment
Представляет внешнее разрушительное событие.
Атрибуты:
- active: boolean
- intensity: int (0..100)

Правила:
- Если `active=true`, то `intensity > 0`.

### 2.2 Environment
Представляет условия окружающей среды.
Атрибуты:
- heatLevel: int (0..100)
- noiseLevel: int (0..100)

Правила:
- `heatLevel` и `noiseLevel` находятся в диапазоне [0..100].

### 2.3 ComputerBank (разрушаемый объект)
Представляет конструкцию «компьютерного банка».
Атрибуты:
- integrity: int (0..100) — степень целостности
- frontMeltPercent: int (0..100) — степень расплавления передней стороны

Правила:
- `integrity` и `frontMeltPercent` находятся в диапазоне [0..100]
- Если `frontMeltPercent >= 90`, поток расплавленного металла возможен или ожидаем
- Если `integrity` уменьшается, количество обломков увеличивается (моделируется косвенно через переходы состояний)

### 2.4 MoltenMetalFlow
Представляет потоки расплавленного металла, движущиеся к углу.
Атрибуты:
- active: boolean
- direction: Corner (TARGET_CORNER / NONE)
- thickness: int (1..10) [условная шкала]

Правила:
- Если `active=true`, то `direction=TARGET_CORNER`, а `thickness` находится в диапазоне [1..10]
- Если `active=false`, то `direction=NONE`

### 2.5 PeopleGroup
Представляет «их», сидящих в углу.
Атрибуты:
- count: int (>0)
- position: Corner (TARGET_CORNER / OTHER / NONE)
- cohesion: int (0..100)
- state: GroupState {SITTING, HUDDLING, WAITING_END}

Правила:
- count > 0
- Если `state = HUDDLING`, то `cohesion >= 50`
- Если поток расплавленного металла направлен в `TARGET_CORNER`, и позиция группы тоже `TARGET_CORNER`, состояние группы становится `HUDDLING` или `WAITING_END`.

## 3. Сервисы предметной области / переходы

### 3.1 Событие: bombardmentResumes()
Эффекты:
- Bombardment.active=true
- Environment.heatLevel и noiseLevel увеличиваются (ограничены 100)
- ComputerBank.integrity уменьшается (ограничено 0)

### 3.2 Событие: meltingProgress()
Если `ComputerBank.frontMeltPercent >= 90`:
- MoltenMetalFlow.active=true
- MoltenMetalFlow.direction=TARGET_CORNER

### 3.3 Событие: moltenMetalApproachesCorner()
Если `MoltenMetalFlow.active`, `direction=TARGET_CORNER` и `PeopleGroup.position=TARGET_CORNER`:
- PeopleGroup.cohesion увеличивается
- PeopleGroup.state переходит в сторону WAITING_END

## 4. Покрытие тестами

Проверяется:
- валидация инвариантов (диапазоны, недопустимые значения)
- переходы состояний при событиях:
  - bombardmentResumes согласованно изменяет состояния
  - meltingProgress активирует поток расплава при сильном плавлении передней части
  - moltenMetalApproachesCorner переводит группу в HUDDLING/WAITING_END
- негативные тесты: недопустимые значения атрибутов вызывают IllegalArgumentException
