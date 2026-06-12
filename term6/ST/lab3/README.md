# Лабораторная работа №3 — функциональное тестирование Google Drive

Вариант **51232**: сайт <https://drive.google.com/>.

## Что тестируем

**Реальный интерфейс Drive после логина** — главная страница, поиск, кнопка
«Создать», меню аккаунта. То есть тот UI, который видит обычный пользователь
Google Drive, а не страницу входа.

## Стек

- Java 17 + Maven
- Selenium WebDriver 4 (Selenium Manager сам качает `chromedriver`/`geckodriver`)
- JUnit Jupiter 5 + `junit-jupiter-params`
- Selenium IDE — шаблоны сценариев в `selenium-ide/google-drive.side`

## Структура

```
lab3/
├── pom.xml                       — Maven-конфигурация
├── README.md                     — этот файл
├── usecases.md                   — список прецедентов и UseCase-диаграмма
├── checklist.md                  — чек-лист тестового покрытия
├── selenium-ide/
│   └── google-drive.side         — сценарии Selenium IDE
└── src/test/java/ru/itmo/lab3/
    ├── BaseTest.java             — настройка браузера, ожидания, helper'ы
    ├── LoginHelper.java          — программный логин в Google (только Chrome)
    ├── CookieJar.java            — сохранение/загрузка cookies сессии
    ├── SaveDriveSession.java     — одноразовый «генератор» cookie-файла
    ├── DriveHomeTest.java        — UC-01: главная страница Drive
    ├── DriveSearchTest.java      — UC-02: поиск
    ├── DriveCreateTest.java      — UC-03: создание элементов
    └── DriveAccountTest.java     — UC-04: меню аккаунта
```

## Как запустить

### Один раз — сгенерировать cookie-сессию

Логин в Google через Selenium блокируется bot-детектором в Firefox, поэтому
логиним только Chrome, а потом передаём cookies в обоих браузерах.

```bash
cd lab3
DRIVE_EMAIL='your.email@gmail.com' DRIVE_PASSWORD='your-password' \
    mvn test -Dtest=SaveDriveSession
```

Это создаст файл `~/.lab3-drive-session` с куками боевой сессии. **Файл лежит
вне репозитория, никогда не коммить его.** Куки живут ~14 дней, потом
повторить.

### Прогнать все тесты

```bash
mvn test -Dtest='!SaveDriveSession'
```

Откроются окна Chrome и Firefox (видимый режим — иначе Google детектит
автоматизацию), пройдут 14 тестов в обоих браузерах. Должно быть:

```
Tests run: 14, Failures: 0, Errors: 0
BUILD SUCCESS
```

## Особенности реализации

### Cookie-сессия вместо повторных логинов

Google блокирует `Selenium + Firefox` сразу после ввода email — даже с
поддельным user-agent и `dom.webdriver.enabled=false`. В Chrome логин проходит,
если запускать в видимом (не headless) режиме.

Решение: логинимся **один раз через Chrome**, сохраняем cookies в файл.
Дальше любой браузер просто загружает эти cookies и приходит в Drive
уже залогиненным — bot-детектор Google для аутентифицированной сессии
куда лояльнее.

### Локаторы только по XPath

ID на странице Drive динамические (`c0`, `W2WQz`, …) — на них нельзя
полагаться. Привязываемся к стабильным признакам:

- атрибуты ввода: `//input[@name='q']`
- текст кнопки: `//button[normalize-space(.)='Создать']`
- ARIA-роли и метки: `//*[@role='menuitem']`,
  `//*[contains(@aria-label,'Аккаунт Google')]`

### Хитрость с cookie-доменом

Cookies на `.google.com` (root) применяются на любом субдомене, а cookies
на `drive.google.com` — только если браузер в момент `addCookie()` находится
именно на `drive.google.com`. Поэтому перед загрузкой cookies мы заходим
на `drive.google.com/robots.txt` (этот URL не редиректит) — это даёт нужный
домен-контекст. После этого все cookies успешно применяются, и переход на
`https://drive.google.com/` идёт уже в залогиненном состоянии.

### Видимый режим браузера

По умолчанию запускаем без `--headless`. Google активно отлично детектит
headless-браузеры и блокирует логин/сессию. Для тестов это неудобно (окна
открываются), но альтернативы нет. Если очень хочется попробовать — есть
флаг `-Dheadless=true`, но логин в нём не пройдёт.

## Selenium IDE

В `selenium-ide/google-drive.side` лежит проект Selenium IDE с теми же
сценариями. Открыть: расширение Selenium IDE для Chrome или Firefox →
`File → Open project → google-drive.side`.

Чтобы получить из `.side` готовый Java-код WebDriver:
`Export tests in… → Java JUnit`. Это и есть путь от Selenium IDE к
Selenium WebDriver, описанный в ТЗ.
