def min_moves_to_correct_brackets(s):
    balance = 0
    min_balance = 0

    for char in s:
        if char == '(':
            balance += 1
        else:
            balance -= 1
        min_balance = min(min_balance, balance)

    return abs(min_balance)


def main():
    t = int(input().strip())
    for _ in range(t):
        input().strip()
        s = input().strip()
        print(min_moves_to_correct_brackets(s))


if __name__ == "__main__":
    main()
