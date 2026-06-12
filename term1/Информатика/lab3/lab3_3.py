import time
start_time = time.time()
def parse_json(json_string):
    # Пропуск пробелов
    def skip_whitespace(idx):
        while idx < len(json_string) and json_string[idx].isspace():
            idx += 1
        return idx

    # Разбор значения
    def parse_value(idx):
        idx = skip_whitespace(idx)
        if json_string[idx] == '{':
            return parse_object(idx)
        elif json_string[idx] == '[':
            return parse_array(idx)
        elif json_string[idx] == '"':
            return parse_string(idx)
        elif json_string[idx].isdigit() or json_string[idx] in '-+':
            return parse_number(idx)

    # Разбор объекта
    def parse_object(idx):
        obj = {} # Словарь
        idx += 1  # Пропускаем открывающую фигурную скобку
        while True:
            idx = skip_whitespace(idx)
            if json_string[idx] == '}':
                return obj, idx + 1
            key, idx = parse_string(idx)
            idx = skip_whitespace(idx)
            idx += 1  # Пропускаем двоеточие
            value, idx = parse_value(idx)
            obj[key] = value
            idx = skip_whitespace(idx)
            if json_string[idx] == ',':
                idx += 1  # Пропускаем запятую
            elif json_string[idx] == '}':
                return obj, idx + 1

    # Разбор массива
    def parse_array(idx):
        arr = []
        idx += 1  # Пропускаем открывающую квадратную скобку
        while True:
            idx = skip_whitespace(idx)
            if json_string[idx] == ']':
                return arr, idx + 1
            value, idx = parse_value(idx)
            arr.append(value)
            idx = skip_whitespace(idx)
            if json_string[idx] == ',':
                idx += 1  # Пропускаем запятую
            elif json_string[idx] == ']':
                return arr, idx + 1

    # Разбор строки
    def parse_string(idx):
        idx += 1  # Пропускаем открывающую кавычку
        start = idx
        while json_string[idx] != '"':
            idx += 1
        return json_string[start:idx], idx + 1

    # Разбор числа
    def parse_number(idx):
        start = idx
        if json_string[idx] in '-+':
            idx += 1
        while idx < len(json_string) and (json_string[idx].isdigit() or json_string[idx] == '.'):
            idx += 1
        number_str = json_string[start:idx]
        return int(number_str), idx # Возвращаем число

    # Разбор JSON и возврат результата
    result, _ = parse_value(0) # Получаем значение и игнорируем индекс
    return result

def to_yaml(data):
    yaml_str = ""
    # Обработка словарей
    if isinstance(data, dict):
        for key, value in data.items():
            if isinstance(value, list):  # Обработка дня недели
                yaml_str += key + ":\n"
                yaml_str += to_yaml(value)  # Рекурсивно вызываем to_yaml для списка уроков
            elif key == 'name':  # Использование значения 'name' в качестве названия урока
                yaml_str += "- " + value + ":\n"
            else:  # Для всех остальных полей используем стандартный формат
                yaml_str += '  ' + key + ": " + "'" + value + "'" + "\n"
    # Обработка списков
    elif isinstance(data, list): # Если данные являются списком, итерируем по элементам списка и рекурсивно вызываем to_yaml
        for item in data:
            yaml_str += to_yaml(item)
    else:
        yaml_str += ' ' + str(data) + "\n" # Для всех остальных типов данных
    return yaml_str

def save_to_yaml_file(yaml_string, output_filename):
    with open(output_filename, 'w') as file:
        file.write(yaml_string)

# Чтение содержимого JSON из файла
file_path = r'C:\Users\Matve\OneDrive\Рабочий стол\schedule.json'
with open(file_path, 'r') as file:
    json_content = file.read()

# Разбор JSON и преобразование в YAML
parsed_json = parse_json(json_content)
yaml_result = to_yaml(parsed_json)

# Сохранение результата в файл YAML
output_file_path = r'C:\Users\Matve\OneDrive\Рабочий стол\schedule.yaml'
save_to_yaml_file(yaml_result, output_file_path)

elapsed_time = time.time() - start_time
print(f"Программа выполнилась за {elapsed_time} секунд")

