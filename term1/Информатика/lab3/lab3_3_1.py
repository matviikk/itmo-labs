import json
import yaml
import time
start_time = time.time()
def convert_json_to_yaml(input_filename, output_filename):
    # Чтение содержимого JSON файла
    with open(input_filename, 'r') as json_file:
        json_data = json.load(json_file)

    # Преобразование данных в YAML формат
    yaml_data = yaml.dump(json_data, default_flow_style=False, allow_unicode=True)

    # Запись YAML данных в файл
    with open(output_filename, 'w') as yaml_file:
        yaml_file.write(yaml_data)

# Указание пути к входному JSON файлу и выходному YAML файлу
input_file = r'C:\Users\Matve\OneDrive\Рабочий стол\schedule.json'
output_file = r'C:\Users\Matve\OneDrive\Рабочий стол\schedule.yaml'

# Вызов функции для конвертации
convert_json_to_yaml(input_file, output_file)

elapsed_time = time.time() - start_time
print(f"Программа выполнилась за {elapsed_time} секунд")
