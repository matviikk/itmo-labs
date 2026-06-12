import sys, bisect

class BIT:
    __slots__ = ('n', 'a')
    def __init__(self, n: int):
        self.n = n
        self.a = [0]*(n+1)

    def add(self, i: int) -> None:
        while i <= self.n:
            self.a[i] += 1
            i += i & -i

    def sum(self, i: int) -> int:
        s = 0
        while i:
            s += self.a[i]
            i -= i & -i
        return s

    def kth(self, k: int) -> int:
        idx = 0
        bit = 1 << (self.n.bit_length())
        while bit:
            nxt = idx + bit
            if nxt <= self.n and self.a[nxt] < k:
                k -= self.a[nxt]
                idx = nxt
            bit >>= 1
        return idx + 1
def main() -> None:
    rl = sys.stdin.buffer.readline
    n  = int(rl())
    arr = list(map(int, rl().split()))

    sorted_vals = sorted(arr)
    rank = {v: i+1 for i, v in enumerate(sorted_vals)}

    bit   = BIT(n)
    timer = [0]*(n+1)
    ans   = [0]*(n-1)

    r0 = rank[arr[0]]
    bit.add(r0)
    timer[r0] = 1
    t = 1

    for i in range(1, n):
        v  = arr[i]
        r  = rank[v]
        t += 1

        less = bit.sum(r-1)
        tot  = i

        pred = bit.kth(less)     if less        else None
        succ = bit.kth(less+1)   if less < tot  else None

        if pred is None:
            par_rank = succ
        elif succ is None:
            par_rank = pred
        else:
            par_rank = pred if timer[pred] > timer[succ] else succ

        ans[i-1] = sorted_vals[par_rank-1]

        bit.add(r)
        timer[r] = t

    sys.stdout.write(' '.join(map(str, ans)))

if __name__ == '__main__':
    main()