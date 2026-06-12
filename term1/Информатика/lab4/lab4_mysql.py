import mysql.connector
from mysql.connector import Error
def create_connection(host_name, user_name, user_password, db_name):
    connection = None
    try:
        connection = mysql.connector.connect(
            host=host_name,
            user=user_name,
            passwd=user_password,
            database=db_name
        )
        print("Connection to MySQL DB successful")
    except Error as e:
        print(f"The error '{e}' occurred")
    return connection

connection = create_connection("localhost", "root", "Matvey2005", "sm_app")

def create_database(connection, query):
    cursor = connection.cursor()
    try:
        cursor.execute(query)
        print("Database created successfully")
    except Error as e:
        print(f"The error '{e}' occurred")

# create_database_query = "CREATE DATABASE sm_app"
# create_database(connection, create_database_query)

def execute_query(connection, query):
    cursor = connection.cursor()
    try:
        cursor.execute(query)
        connection.commit()
        print("Query executed successfully")
    except Error as e:
        print(f"The error '{e}' occurred")

# Создание таблиц
# create_users_table = """
# CREATE TABLE IF NOT EXISTS users (
#     id INT AUTO_INCREMENT PRIMARY KEY,
#     name VARCHAR(255) NOT NULL,
#     age INT,
#     gender VARCHAR(50),
#     country VARCHAR(100)
# );
# """
# execute_query(connection, create_users_table)
#
# create_reviews_table = """
# CREATE TABLE IF NOT EXISTS reviews (
#     id INT AUTO_INCREMENT PRIMARY KEY,
#     title VARCHAR(255) NOT NULL,
#     description TEXT NOT NULL,
#     user_id INT NOT NULL,
#     FOREIGN KEY (user_id) REFERENCES users (id)
# );
# """
# execute_query(connection, create_reviews_table)
#
# create_answers_table = """
# CREATE TABLE IF NOT EXISTS answers (
#     id INT AUTO_INCREMENT PRIMARY KEY,
#     text TEXT NOT NULL,
#     user_id INT NOT NULL,
#     review_id INT NOT NULL,
#     FOREIGN KEY (user_id) REFERENCES users (id),
#     FOREIGN KEY (review_id) REFERENCES reviews (id)
# );
# """
# execute_query(connection, create_answers_table)
#
# create_likes_table = """
# CREATE TABLE IF NOT EXISTS likes (
#     user_id INT NOT NULL,
#     review_id INT NOT NULL,
#     FOREIGN KEY (user_id) REFERENCES users (id),
#     FOREIGN KEY (review_id) REFERENCES reviews (id)
# );
# """
# execute_query(connection, create_likes_table)
#
# create_users = """
# INSERT INTO
#     `users` (`name`, `age`, `gender`, `country`)
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
#     `reviews` (title, description, user_id)
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
#     `answers` (text, user_id, review_id)
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
#     `likes` (user_id, review_id)
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

# select_users = "SELECT * FROM users"
# users = execute_read_query(connection, select_users)
# for user in users:
#     print(user)
#
# update_review_description = """
# UPDATE
#     posts
# SET
#     description = "The weather has become pleasant now"
# WHERE
#     id = 2
# """
# execute_query(connection, update_review_description)

# Добавление нового пользователя в таблицу 'users'
insert_user_query = """
INSERT INTO users (name, age, gender, country)
VALUES ('Sam', 28, 'male', 'USA');
"""
execute_query(connection, insert_user_query)

# Добавление нового отзыва в таблицу 'reviews'
insert_review_query = """
INSERT INTO reviews (title, description, user_id)
VALUES ('Great Product', 'I loved this product, it was amazing!', 1);
"""
execute_query(connection, insert_review_query)

# Добавление нового ответа в таблицу 'answers'
insert_answer_query = """
INSERT INTO answers (text, user_id, review_id)
VALUES ('Thanks for the review!', 1, 1);
"""
execute_query(connection, insert_answer_query)

# Добавление нового лайка в таблицу 'likes'
insert_like_query = """
INSERT INTO likes (user_id, review_id)
VALUES (1, 1);
"""
execute_query(connection, insert_like_query)

# Выбор всех записей из таблиц
print('\nВыбор всех записей из таблиц')
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

# Запросы с использованием JOIN, WHERE и GROUP BY
print('\nЗапросы с использованием JOIN')
query = """
SELECT users.name, reviews.title, reviews.description
FROM users
JOIN reviews ON users.id = reviews.user_id
"""
results = execute_read_query(connection, query)
for result in results:
    print(result)

print('\nЗапросы с использованием WHERE и GROUP BY')
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

# Запросы с вложенным SELECT
print('\nЗапросы с вложенным SELECT')
query = """
SELECT * FROM users
WHERE id IN (SELECT user_id FROM reviews WHERE CHAR_LENGTH(description) > 50)
"""
results = execute_read_query(connection, query)
for result in results:
    print(result)

query = """
SELECT * FROM reviews
WHERE user_id IN (SELECT id FROM users WHERE age < 30)
"""
results = execute_read_query(connection, query)
for result in results:
    print(result)

# Запросы с использованием UNION
print('\nЗапросы с использованием UNION')
query = """
SELECT name FROM users
UNION
SELECT title FROM reviews
"""
results = execute_read_query(connection, query)
for result in results:
    print(result)

query = """
SELECT user_id FROM reviews
UNION
SELECT user_id FROM likes
"""
results = execute_read_query(connection, query)
for result in results:
    print(result)

# Запрос с использованием DISTINCT
print('\nЗапрос с использованием DISTINCT')
query = """
SELECT DISTINCT country FROM users
"""
results = execute_read_query(connection, query)
for result in results:
    print(result)

# Обновление записей
print('\nОбновление записей')
update_user_query = """
UPDATE users
SET age = 26
WHERE name = 'James'
"""
update_review_query = """
UPDATE reviews
SET description = 'Survived a rugged month-long trek and still looks as good as new!'
WHERE title = 'Extremely Durable'
"""
execute_query(connection, update_user_query)
execute_query(connection, update_review_query)

# Проверка обновлённых записей
print('\nПроверка обновлённых записей')
select_users_query = "SELECT * FROM users WHERE name = 'James'"
select_reviews_query = "SELECT * FROM reviews WHERE title = 'Extremely Durable'"

users_results = execute_read_query(connection, select_users_query)
reviews_results = execute_read_query(connection, select_reviews_query)

print("Updated users:")
for user in users_results:
    print(user)

print("\nUpdated reviews:")
for review in reviews_results:
    print(review)

# Удаление записей
print('\nУдаление записей')
delete_user_query = "DELETE FROM users WHERE name = 'James'"
delete_review_query = "DELETE FROM reviews WHERE title = 'Extremely Durable'"
delete_answer_query = "DELETE FROM answers WHERE text = 'Thank you for your feedback!'"
delete_like_query = "DELETE FROM likes WHERE user_id = 1 AND review_id = 6"

execute_query(connection, delete_user_query)
execute_query(connection, delete_review_query)
execute_query(connection, delete_answer_query)
execute_query(connection, delete_like_query)

# Проверка таблиц после удаления записей
print('\nПроверка таблиц после удаления записей')
query = "SELECT * FROM users"
results = execute_read_query(connection, query)
print('Таблица users')
for result in results:
    print(result)

query = "SELECT * FROM reviews"
results = execute_read_query(connection, query)
print('\nТаблица reviews')
for result in results:
    print(result)

query = "SELECT * FROM answers"
results = execute_read_query(connection, query)
print('\nТаблица answers')
for result in results:
    print(result)

query = "SELECT * FROM likes"
results = execute_read_query(connection, query)
print('\nТаблица likes')
for result in results:
    print(result)

# Удаление всех записей из таблицы likes
print('\nУдаление всех записей из таблицы likes')
delete_all_likes_query = "DELETE FROM likes;"
execute_query(connection, delete_all_likes_query)

# Проверка таблицы likes после удаления всех записей
print('\nРезультат удаления записей из таблицы likes')
query = "SELECT * FROM likes"
results = execute_read_query(connection, query)
for result in results:
    print(result)

# def delete_database(connection, db_name):
#     cursor = connection.cursor()
#     try:
#         cursor.execute(f"DROP DATABASE IF EXISTS {db_name};")
#         print(f"Database {db_name} deleted successfully")
#     except mysql.connector.Error as e:
#         print(f"Error occurred: {e}")
#
#
# delete_database(connection, "sm_app")
