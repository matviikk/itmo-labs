def main():
    s = input().strip()
    stack = []
    open_to_close = {'(': ')', '[': ']', '{': '}', '<': '>'}
    close_to_open = {')': '(', ']': '[', '}': '{', '>': '<'}
    replacements = 0

    for char in s:
        if char in open_to_close:
            stack.append(char)
        else:
            if not stack:
                print("Impossible")
                return
            top = stack.pop()
            if open_to_close[top] != char:
                replacements += 1

    if stack:
        print("Impossible")
    else:
        print(replacements)

if __name__ == "__main__":
    main()