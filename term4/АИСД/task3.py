def solve():
    line = input().strip()
    tokens = line.split()

    eq_index = tokens.index('=')

    n = int(tokens[-1])

    expr_tokens = tokens[:eq_index]

    question_signs = []
    question_positions = []

    current_sign = +1
    for i, tk in enumerate(expr_tokens):
        if tk == '?':
            question_signs.append(current_sign)
            question_positions.append(i)
        elif tk == '+':
            current_sign = +1
        elif tk == '-':
            current_sign = -1

    m = len(question_signs)

    cnt_plus = sum(1 for s in question_signs if s == +1)
    cnt_minus = m - cnt_plus

    min_possible_sum = cnt_plus * 1 - cnt_minus * n
    max_possible_sum = cnt_plus * n - cnt_minus * 1

    if not (min_possible_sum <= n <= max_possible_sum):
        print("Impossible")
        return

    print("Possible")

    assigned = [0] * m
    partial_sum = 0

    plus_left = cnt_plus
    minus_left = cnt_minus

    for i in range(m):
        s = question_signs[i]
        if s == +1:
            plus_left -= 1
        else:
            minus_left -= 1

        min_rem = plus_left * 1 - minus_left * n
        max_rem = plus_left * n - minus_left * 1

        need_low = (n - partial_sum) - max_rem
        need_high = (n - partial_sum) - min_rem

        if s == +1:
            low_x = need_low
            high_x = need_high
        else:
            low_x = -need_high
            high_x = -need_low

        x_min = max(1, low_x)
        x_max = min(n, high_x)

        if x_min > x_max:
            print("Impossible")
            return

        x_i = x_min
        assigned[i] = x_i
        partial_sum += s * x_i

    result_tokens = expr_tokens[:]
    q_idx = 0
    for pos in question_positions:
        result_tokens[pos] = str(assigned[q_idx])
        q_idx += 1

    print(' '.join(result_tokens) + ' = ' + str(n))


if __name__ == "__main__":
    solve()
