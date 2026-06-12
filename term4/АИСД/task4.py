from collections import defaultdict


def build_spf(n):
    spf = list(range(n + 1))
    spf[0] = 0
    spf[1] = 1
    for i in range(2, int(n ** 0.5) + 1):
        if spf[i] == i:
            for j in range(i * i, n + 1, i):
                if spf[j] == j:
                    spf[j] = i
    return spf


def factorize(x, spf):
    res = defaultdict(int)
    while x > 1:
        p = spf[x]
        res[p] += 1
        x //= p
    return res


def solve():
    t = int(input().strip())
    MAXA = 10 ** 6
    spf = build_spf(MAXA)

    answers = []
    for _ in range(t):
        n = int(input().strip())
        arr = list(map(int, input().split()))

        prime_count = defaultdict(int)

        for val in arr:
            fct = factorize(val, spf)
            for p, e in fct.items():
                prime_count[p] += e

        possible = True
        for e in prime_count.values():
            if e % n != 0:
                possible = False
                break

        answers.append("YES" if possible else "NO")

    print("\n".join(answers))


if __name__ == "__main__":
    solve()
