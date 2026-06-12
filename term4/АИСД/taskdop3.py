import sys
from collections import deque

def solve_case(a: list[int]) -> tuple[int, int]:
    n = len(a)
    adj = [set() for _ in range(n)]
    for i, v in enumerate(a):
        v -= 1
        adj[i].add(v)
        adj[v].add(i)

    seen = [False] * n
    cycles = paths = 0

    for s in range(n):
        if seen[s]:
            continue
        q = deque([s])
        seen[s] = True
        component = []
        while q:
            u = q.popleft()
            component.append(u)
            for w in adj[u]:
                if not seen[w]:
                    seen[w] = True
                    q.append(w)
        if all(len(adj[v]) == 2 for v in component):
            cycles += 1
        else:
            paths += 1

    max_cnt = cycles + paths
    min_cnt = cycles + (1 if paths else 0)
    return min_cnt, max_cnt


def read_ints_needed(n: int) -> list[int]:
    res = []
    while len(res) < n:
        line = sys.stdin.readline()
        if not line:
            raise ValueError("Неожиданный конец ввода")
        res.extend(map(int, line.split()))
    return res


def main() -> None:
    t = int(sys.stdin.readline())
    for _ in range(t):
        n = int(sys.stdin.readline())
        a = read_ints_needed(n)
        mn, mx = solve_case(a)
        print(mn, mx)


if __name__ == "__main__":
    main()