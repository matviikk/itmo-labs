n = int(input())
names = {}

for _ in range(n):
    name = input()
    if name not in names:
        names[name] = 1
        print("OK")
    else:
        new_name = name + str(names[name])
        while new_name in names:
            names[name] += 1
            new_name = name + str(names[name])
        print(new_name)
        names[name] += 1
        names[new_name] = 1