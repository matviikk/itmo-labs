import sqlite3
from sqlite3 import Error
def create_connection(path):
    connection = None
    try:
        connection = sqlite3.connect(path)
        print("Connection to SQLite DB successful")
    except Error as e:
        print(f"The error '{e}' occurred")
    return connection

connection = create_connection("D:\\sm_app.sqlite")

def execute_query(connection, query):
    cursor = connection.cursor()
    try:
        cursor.execute(query)
        connection.commit()
        print("Query executed successfully")
    except Error as e:
        print(f"The error '{e}' occurred")
#
# create_users_table = """
# CREATE TABLE IF NOT EXISTS users (
#     id INTEGER PRIMARY KEY AUTOINCREMENT,
#     name TEXT NOT NULL,
#     age INTEGER,
#     gender TEXT,
#     country TEXT
# );
# """
# execute_query(connection, create_users_table)
#
# create_reviews_table = """
# CREATE TABLE IF NOT EXISTS reviews(
#     id INTEGER PRIMARY KEY AUTOINCREMENT,
#     title TEXT NOT NULL,
#     description TEXT NOT NULL,
#     user_id INTEGER NOT NULL,
#     FOREIGN KEY (user_id) REFERENCES users (id)
# );
# """
# execute_query(connection, create_reviews_table)
#
# create_answers_table = """
# CREATE TABLE IF NOT EXISTS answers (
#     id INTEGER PRIMARY KEY AUTOINCREMENT,
#     text TEXT NOT NULL,
#     user_id INTEGER NOT NULL,
#     review_id INTEGER NOT NULL,
#     FOREIGN KEY (user_id) REFERENCES users (id) FOREIGN KEY (review_id)
# REFERENCES reviews (id)
# );
# """
# execute_query(connection, create_answers_table)
#
# create_likes_table = """
# CREATE TABLE IF NOT EXISTS likes (
#     id INTEGER PRIMARY KEY AUTOINCREMENT,
#     user_id INTEGER NOT NULL,
#     review_id integer NOT NULL,
#     FOREIGN KEY (user_id) REFERENCES users (id) FOREIGN KEY (review_id)
# REFERENCES reviews (id)
# );
# """
# execute_query(connection, create_likes_table)
#
# create_users = """
# INSERT INTO
#     users (name, age, gender, country)
# VALUES
#     ('James', 25, 'male', 'USA'),
#     ('Leila', 32, 'female', 'France'),
#     ('Brigitte', 35, 'female', 'England'),
#     ('Mike', 40, 'male', 'Denmark'),
#     ('Elizabeth', 21, 'female', 'Canada');
# """
# execute_query(connection, create_users)
#
# create_reviews = """
# INSERT INTO
#     reviews (title, description, user_id)
# VALUES
#     ("Extremely Durable", "Survived a rugged month-long trek without a scratch!", 1),
#     ("Back Pain Reliever", "A week in and my back pain has disappeared.", 2),
#     ("Silence Redefined", "Blocks out commute noise perfectly, battery lasts ages.", 2),
#     ("Precision Cutting", "Exceptionally sharp and well-balanced for easy prep work.", 1),
#     ("Swim-Ready Tech", "Daily swims for two weeks and it tracks my fitness flawlessly.", 5),
#     ("Pet Approved", "My dog loves it and looks healthier than ever!", 3);
# """
# execute_query(connection, create_reviews)
#
# create_answers = """
# INSERT INTO
#     answers (text, user_id, review_id)
# VALUES
#     ('Thank you for your feedback!', 1, 6),
#     ('We are glad that you liked the product:)', 5, 3),
#     ('We were glad to help:з', 2, 4),
#     ('Nice to hear)', 4, 5),
#     ('Thank you, we are trying our best!', 2, 3),
#     ('Come again;)', 5, 4);
# """
# execute_query(connection, create_answers)
#
# create_likes = """
# INSERT INTO
#     likes (user_id, review_id)
# VALUES
#     (1, 6),
#     (2, 3),
#     (1, 5),
#     (5, 4),
#     (2, 4),
#     (4, 2),
#     (3, 6);
# """
# execute_query(connection, create_likes)

def execute_read_query(connection, query):
    cursor = connection.cursor()
    result = None
    try:
        cursor.execute(query)
        result = cursor.fetchall()
        return result
    except Error as e:
        print(f"The error '{e}' occurred")

# select_users = "SELECT * from users"
# users = execute_read_query(connection, select_users)
# for user in users:
#     print(user)
#
# print('\n')
#
# select_reviews = "SELECT * from reviews"
# reviews = execute_read_query(connection, select_reviews)
# for review in reviews:
#     print(review)
#
# print('\n')
#
# select_users_reviews = """
# SELECT
#     users.id,
#     users.name,
#     reviews.description
# FROM
#     reviews
#     INNER JOIN users ON users.id = reviews.user_id
# """
# users_reviews = execute_read_query(connection, select_users_reviews)
# for users_review in users_reviews:
#     print(users_review)
#
# print('\n')
#
# select_reviews_answers_users = """
# SELECT
#     reviews.description as review,
#     text as answer,
#     name
# FROM
#     reviews
#     INNER JOIN answers ON reviews.id = answers.review_id
#     INNER JOIN users ON users.id = answers.user_id
# """
# reviews_answers_users = execute_read_query(connection, select_reviews_answers_users)
# for reviews_answers_user in reviews_answers_users:
#     print(reviews_answers_user)
#
# cursor = connection.cursor()
# cursor.execute(select_reviews_answers_users)
# cursor.fetchall()
# column_names = [description[0] for description in cursor.description]
# print(column_names)
#
# print('\n')

# select_review_likes = """
# SELECT
#     description as Review,
#     COUNT(likes.id) as Likes
# FROM
#     likes,
#     reviews
# WHERE
#     reviews.id = likes.review_id
# GROUP BY
#     likes.review_id
# """
# review_likes = execute_read_query(connection, select_review_likes)
# for review_like in review_likes:
#     print(review_like)
#
# select_review_description = "SELECT description FROM reviews WHERE id = 2"
# review_description = execute_read_query(connection, select_review_description)
# for description in review_description:
#     print(description)
#
# update_review_description = """
# UPDATE
#     reviews
# SET
#     description = "The weather has become pleasant now"
# WHERE
#     id = 2
# """
# execute_query(connection, update_review_description)
#
# select_review_description = "SELECT description FROM reviews WHERE id = 2"
# review_description = execute_read_query(connection, select_review_description)
# for description in review_description:
#     print(description)
#
# delete_answer = "DELETE FROM answers WHERE id = 5"
# execute_query(connection, delete_answer)

# Добавьте по одной новой записи в каждую из таблиц Вашей базы данных
# Добавление нового пользователя в таблицу 'users'
insert_user_query = """
INSERT INTO users (name, age, gender, country)
VALUES ('Sam', 28, 'male', 'USA');
"""

# Добавление нового отзыва в таблицу 'reviews'
insert_review_query = """
INSERT INTO reviews (title, description, user_id)
VALUES ('Great Product', 'I loved this product, it was amazing!', 1);
"""

# Добавление нового ответа в таблицу 'answers'
insert_answer_query = """
INSERT INTO answers (text, user_id, review_id)
VALUES ('Thanks for the review!', 1, 1);
"""

# Добавление нового лайка в таблицу 'likes'
insert_like_query = """
INSERT INTO likes (user_id, review_id)
VALUES (1, 1);
"""

execute_query(connection, insert_user_query)
execute_query(connection, insert_review_query)
execute_query(connection, insert_answer_query)
execute_query(connection, insert_like_query)


# Выбрать все записи из таблиц
print('\nВыбрать все записи из таблиц')
print('Таблица users')
query = "SELECT * FROM users"
results = execute_read_query(connection, query)
for result in results:
    print(result)

print('\nТаблица reviews')
query = "SELECT * FROM reviews"
results = execute_read_query(connection, query)
for result in results:
    print(result)

print('\nТаблица answers')
query = "SELECT * FROM answers"
results = execute_read_query(connection, query)
for result in results:
    print(result)

print('\nТаблица likes')
query = "SELECT * FROM likes"
results = execute_read_query(connection, query)
for result in results:
    print(result)

# Составить запрос по извлечению данных с использованием JOIN
print('\nСоставить запрос по извлечению данных с использованием JOIN')
query = """
SELECT users.name, reviews.title, reviews.description
FROM users
JOIN reviews ON users.id = reviews.user_id
"""
results = execute_read_query(connection, query)
for result in results:
    print(result)

# Составить запрос по извлечению данных с использованием WHERE и GROUP BY
print('\nСоставить запрос по извлечению данных с использованием WHERE и GROUP BY')
query = """
SELECT users.name, COUNT(reviews.id) as review_count
FROM users
JOIN reviews ON users.id = reviews.user_id
WHERE users.age > 30
GROUP BY users.name
"""
results = execute_read_query(connection, query)
for result in results:
    print(result)

# Составить 2 запроса, в которых будет вложенный SELECT-запрос (вложение с помощью WHERE)
print('\nСоставить 2 запроса, в которых будет вложенный SELECT-запрос (вложение с помощью WHERE)')
query = """
SELECT * FROM users
WHERE id IN (SELECT user_id FROM reviews WHERE length(description) > 50)
"""
results = execute_read_query(connection, query)
for result in results:
    print(result)
print('\n')
query = """
SELECT * FROM reviews
WHERE user_id IN (SELECT id FROM users WHERE age < 30)
"""
results = execute_read_query(connection, query)
for result in results:
    print(result)

# Cоставить 2 запроса с использованием UNION (объединение запросов)
print('\nCоставить 2 запроса с использованием UNION (объединение запросов)')
# Объединение имен пользователей и названий отзывов
query = """
SELECT name FROM users
UNION
SELECT title FROM reviews
"""
results = execute_read_query(connection, query)
for result in results:
    print(result)
print('\n')
# Объединение id пользователей из двух таблиц
query = """
SELECT user_id FROM reviews
UNION
SELECT user_id FROM likes
"""
results = execute_read_query(connection, query)
for result in results:
    print(result)

# Составить 1 запрос с использованием DISTINCT
print('\nСоставить 1 запрос с использованием DISTINCT')
query = """
SELECT DISTINCT country FROM users
"""
results = execute_read_query(connection, query)
for result in results:
    print(result)

# Обновить две записи в двух разных таблицах Вашей базы данных
# Обновление возраста пользователя 'James' в таблице 'users'
update_user_query = """
UPDATE users
SET age = 26
WHERE name = 'James'
"""
# Обновление описания отзыва с заголовком 'Extremely Durable' в таблице 'reviews'
update_review_query = """
UPDATE reviews
SET description = 'Survived a rugged month-long trek and still looks as good as new!'
WHERE title = 'Extremely Durable'
"""
execute_query(connection, update_user_query)
execute_query(connection, update_review_query)
# После обновления записи пользователя 'James'
select_users_query = "SELECT * FROM users WHERE name = 'James'"
# После обновления записи отзыва с заголовком 'Extremely Durable'
select_reviews_query = "SELECT * FROM reviews WHERE title = 'Extremely Durable'"

users_results = execute_read_query(connection, select_users_query)
reviews_results = execute_read_query(connection, select_reviews_query)

print("Updated users:")
for user in users_results:
    print(user)

print("\nUpdated reviews:")
for review in reviews_results:
    print(review)

# Удалить по одной записи из каждой таблицы
print('\nУдалить по одной записи из каждой таблицы')
# Удаление пользователя с именем 'James' из таблицы 'users'
delete_user_query = "DELETE FROM users WHERE name = 'James'"
# Удаление отзыва с названием 'Extremely Durable' из таблицы 'reviews'
delete_review_query = "DELETE FROM reviews WHERE title = 'Extremely Durable'"
# Удаление ответа с текстом 'Thank you for your feedback!' из таблицы 'answers'
delete_answer_query = "DELETE FROM answers WHERE text = 'Thank you for your feedback!'"
# Удаление лайка, где user_id = 1 и review_id = 6 из таблицы 'likes'
delete_like_query = "DELETE FROM likes WHERE user_id = 1 AND review_id = 6"

execute_query(connection, delete_user_query)
execute_query(connection, delete_review_query)
execute_query(connection, delete_answer_query)
execute_query(connection, delete_like_query)
print('\nТаблицы после удаления записей:')
print('Таблица users')
query = "SELECT * FROM users"
results = execute_read_query(connection, query)
for result in results:
    print(result)

print('\nТаблица reviews')
query = "SELECT * FROM reviews"
results = execute_read_query(connection, query)
for result in results:
    print(result)

print('\nТаблица answers')
query = "SELECT * FROM answers"
results = execute_read_query(connection, query)
for result in results:
    print(result)

print('\nТаблица likes')
query = "SELECT * FROM likes"
results = execute_read_query(connection, query)
for result in results:
    print(result)

# Удалите все записи в одной из таблиц
print('\nУдалите все записи в одной из таблиц')
# Удаление всех записей из таблицы likes
print('\nУдаление всех записей из таблицы likes')
delete_all_likes_query = "DELETE FROM likes;"
execute_query(connection, delete_all_likes_query)
print('\nРезультат удаления:')
query = "SELECT * FROM likes"
results = execute_read_query(connection, query)
for result in results:
    print(result)