# ТАБЛИЦА ТРАССИРОВКИ — B-Tree (t=2)

Вариант: 342951  
Модуль: `ru.itmo.lab1.BTree` (insert + contains), трассировка через `TracePoint`.

Обозначения TracePoint:
- INSERT_START
- ROOT_FULL_SPLIT
- INSERT_INTO_LEAF / INSERT_INTO_INTERNAL
- DESCEND_TO_CHILD
- SPLIT_CHILD_START / SPLIT_CHILD_PROMOTE_MIDDLE / SPLIT_CHILD_DONE
- SEARCH_START / SEARCH_DESCEND / SEARCH_HIT / SEARCH_MISS

Параметры дерева для всех сценариев: `t = 2`.

---

## Набор данных A — вставка без split (3 ключа)

Вставки (по порядку):
1) K[10]
2) K[20]
3) K[5]

Ожидаемая трассировка:
- INSERT_START, INSERT_INTO_LEAF
- INSERT_START, INSERT_INTO_LEAF
- INSERT_START, INSERT_INTO_LEAF

Комментарий:
Корень является листом и не переполняется (макс. число ключей = 3).

---

## Набор данных B — split корня (4-й ключ)

Вставки:
1) K[10]
2) K[20]
3) K[5]
4) K[6]

Ожидаемая трассировка по вставкам:
1) INSERT_START, INSERT_INTO_LEAF
2) INSERT_START, INSERT_INTO_LEAF
3) INSERT_START, INSERT_INTO_LEAF
4) INSERT_START, ROOT_FULL_SPLIT, SPLIT_CHILD_START, SPLIT_CHILD_PROMOTE_MIDDLE, SPLIT_CHILD_DONE,
   INSERT_INTO_INTERNAL, DESCEND_TO_CHILD, INSERT_INTO_LEAF

Комментарий:
После 3 вставок корень заполнен (3 ключа). На 4-й вставке происходит split корня.

---

## Набор данных C — split дочернего узла при вставке (t=2)

Вставки:
1) K[10]
2) K[20]
3) K[5]
4) K[6]
5) K[12]
6) K[30]
7) K[25]

Ожидаемая трассировка для вставок 1..7:
1) INSERT_START, INSERT_INTO_LEAF
2) INSERT_START, INSERT_INTO_LEAF
3) INSERT_START, INSERT_INTO_LEAF
4) INSERT_START, ROOT_FULL_SPLIT, SPLIT_CHILD_START, SPLIT_CHILD_PROMOTE_MIDDLE, SPLIT_CHILD_DONE,
   INSERT_INTO_INTERNAL, DESCEND_TO_CHILD, INSERT_INTO_LEAF
5) INSERT_START, INSERT_INTO_INTERNAL, DESCEND_TO_CHILD, INSERT_INTO_LEAF
6) INSERT_START, INSERT_INTO_INTERNAL, DESCEND_TO_CHILD, INSERT_INTO_LEAF
7) INSERT_START, INSERT_INTO_INTERNAL, DESCEND_TO_CHILD,
   SPLIT_CHILD_START, SPLIT_CHILD_PROMOTE_MIDDLE, SPLIT_CHILD_DONE,
   INSERT_INTO_LEAF

---

## Набор данных D — трассировка поиска (contains)

После вставок из набора данных C выполнить:
- contains(K[12]) -> ожидается HIT
- contains(K[13]) -> ожидается MISS

Ожидаемые трассировки:
contains(K[12]):
- SEARCH_START, SEARCH_DESCEND, SEARCH_HIT

contains(K[13]):
- SEARCH_START, SEARCH_DESCEND, SEARCH_MISS

Комментарий:
Корень после набора данных C является внутренним узлом, поэтому ожидается как минимум один SEARCH_DESCEND.
