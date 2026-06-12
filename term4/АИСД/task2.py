def solve():
    from math import ceil
    n = int(input().strip())
    if n < 2:
        print(0)
        return
    for k in range(10, 0, -1):
        M = 10**k
        if M - 1 > 2*n:
            continue
        T = (2*n - (M - 1)) // M
        if T < 0:
            continue
        m_ = M // 2
        r = m_ - 1
        x = n + 2 - M
        if x > 0:
            boundary = ceil(x / M)
        else:
            boundary = 0
        if boundary > T + 1:
            boundary = T + 1
        sumCase1 = 0
        if boundary > 0:
            count1 = boundary
            sum_t = (count1 - 1) * count1 // 2
            sumCase1 = count1 * r + m_ * sum_t
        T2 = (n - m_) // m_ if (n - m_) >= 0 else -1
        U = min(T, T2)
        sumCase2 = 0
        if boundary <= U:
            c = (n + 1) - m_
            count2 = (U - boundary + 1)
            sum_t_boundary = boundary * (boundary - 1) // 2 if boundary > 0 else 0
            sum_t_u = U * (U + 1) // 2
            sum_t_segment = sum_t_u - sum_t_boundary
            sumCase2 = count2 * c - m_ * sum_t_segment
        total_pairs = sumCase1 + sumCase2
        if total_pairs > 0:
            print(total_pairs)
            return
    print(n * (n - 1) // 2)

if __name__ == "__main__":
    solve()
