first, last = input().split()
logins = [first[:i] + last[:j] for i in range(1, len(first)+1) for j in range(1, len(last)+1)]
print(min(logins))