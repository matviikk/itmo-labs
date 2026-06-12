import re
def find_fragment(text):
    match = re.search(r'\bВТ\b(?:\W+\w+){0,4}\W+ИТМО\b', text)
    if match:
        return match.group()
    return None

tests = [
    ("А ты знал, что ПИиКТ – лучший факультет в ИТМО?", "ПИиКТ – лучший факультет в ИТМО"),
    ("ИТМО – это не просто ВТ, это лучший вуз в России!", None),
    ("Кто бы мог подумать, что ВТ в этом году в ИТМО будет так много нововведений?", "ВТ в этом году в ИТМО"),
    ("В ИТМО учатся лучшие студенты ВТ.", None),
    ("Этот проект ВТ был разработан в лаборатории ИТМО.", "ВТ был разработан в лаборатории ИТМО")
]

for test in tests:
    text, expected = test
    result = find_fragment(text)
    print(f"Текст: {text}")
    print(f"Найденный фрагмент: {result}")
    print()