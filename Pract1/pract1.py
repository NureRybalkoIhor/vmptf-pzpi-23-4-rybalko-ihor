# І рівень
# Створіть програму, яка приймає два числа від користувача та виводить їх суму.
def SumScore(num1, num2):
    return num1 + num2

num_1 = int(input('Enter first number: '))
num_2 = int(input('Enter second number: '))
result = SumScore(num_1, num_2)
print(f'The sum is: {result}')

# ІI рівень
# Реалізуйте програму, яка визначає, чи є введене користувачем число простим.
def is_prime(num):
    if num <= 1:
        return False
    for i in range(2, num):
        if num % i == 0:
            return False
    return True

is_prime_num = int(input('Enter a number to check if it is prime: '))
print(f'Is {is_prime_num} a prime number? {is_prime(is_prime_num)}')

# ІІІ рівень
# Створіть клас "Калькулятор" з методами для додавання, 
# віднімання, множення та ділення. 
# Виведіть результат обчислень для певного прикладу.

class Calculator:
    def __init__(self, num1, num2):
        self.num1 = num1
        self.num2 = num2
        
    def add(self):
        return self.num1 + self.num2
    
    def subtract(self):
        return self.num1 - self.num2
    
    def multiply(self):
        return self.num1 * self.num2
    
    def divide(self):
        if self.num2 != 0:
            return self.num1 / self.num2
        else:
            return 'Cannot divide by zero'

num1 = int(input('Enter first number: '))
num2 = int(input('Enter second number: '))
calculator = Calculator(num1, num2)
print(f'Addition: {calculator.add()}')
print(f'Subtraction: {calculator.subtract()}')
print(f'Multiplication: {calculator.multiply()}')
print(f'Division: {calculator.divide()}')

# ІV рівень
# Створіть клас "Книготека" з можливістю додавання 
# та видалення книг, а також виведення списку усіх книг.
class Bookshelf:
    def __init__(self):
        self.books = []
        
    def watch_books(self):
        if len(self.books) == 0:
            print('The bookshelf is empty.')
        else:
            print(self.books)
    
    def add_book(self, book):
        self.books.append(book)
        print(f'Book "{book}" added to the bookshelf.')
        
    def remove_book(self, book):
        if book in self.books:
            self.books.remove(book)
            print(f'Book "{book}" removed from the bookshelf.')
        else:
            print(f'Book "{book}" not found in the bookshelf.')

def control_bookshelf():
    bookshelf = Bookshelf()
    while True:
        action = input('Enter action (watch, add, remove, exit): ')
        if action == 'watch':
            bookshelf.watch_books()
        elif action == 'add':
            book = input('Enter book name to add: ')
            bookshelf.add_book(book)
        elif action == 'remove':
            book = input('Enter book name to remove: ')
            bookshelf.remove_book(book)
        elif action == 'exit':
            break
        else:
            print('Invalid action. Please try again.')
            
control_bookshelf()