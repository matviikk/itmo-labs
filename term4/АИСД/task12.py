from collections import Counter
def f(x):
    return len(str(x))
def main():
    t = int(input())
    for _ in range(t):
        n = int(input())
        a = list(map(int, input().split()))
        b = list(map(int, input().split()))

        ca = Counter(a)
        cb = Counter(b)

        operations = 0

        for key in list(ca.keys()):
            common = min(ca[key], cb.get(key, 0))
            ca[key] -= common
            cb[key] -= common
            if cb.get(key) == 0:
                cb.pop(key, None)
            if ca[key] == 0:
                ca.pop(key, None)

        def process(counter):
            nonlocal operations
            new_counter = Counter()
            for key in list(counter.keys()):
                if key >= 10:
                    cnt = counter[key]
                    operations += cnt
                    new_counter[f(key)] += cnt
                    counter[key] = 0
            counter += new_counter

        process(ca)
        process(cb)

        for key in list(ca.keys()):
            common = min(ca[key], cb.get(key, 0))
            ca[key] -= common
            cb[key] -= common
            if cb.get(key) == 0:
                cb.pop(key, None)
            if ca[key] == 0:
                ca.pop(key, None)

        for key in ca:
            if key != 1:
                operations += ca[key]
        for key in cb:
            if key != 1:
                operations += cb[key]

        print(operations)


if __name__ == "__main__":
    main()