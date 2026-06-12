import re
def count_smileys(text):
    # ;<{(
    pattern = r';<{\('
    return len(re.findall(pattern, text))

# Тестовые примеры
test1 = "Hello ;<{( World! ;<{( How are you? ;<{( ;<{("
test2 = "No smile here!"
test3 = ";<{(;<{(;<{(;<{(;<{("
test4 = ";<{("
test5 = ";<{(;<{(;<{("

print(count_smileys(test1))  # 4
print(count_smileys(test2))  # 0
print(count_smileys(test3))  # 5
print(count_smileys(test4))  # 1
print(count_smileys(test5))  # 3
