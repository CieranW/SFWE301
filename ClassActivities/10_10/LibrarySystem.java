public class LibrarySystem {
    public static void main(String[] args) {
        Book book1 = new Book("The Great Gatsby", "F. Scott Fitzgerald");
        Book book2 = new Book("To Kill a Mockingbird", "Harper Lee");
        Book book3 = new Book("1984", "George Orwell");
        Book book4 = new Book("Pride and Prejudice", "Jane Austen");
        Book book5 = new Book("The Catcher in the Rye", "J.D. Salinger");

        book1.borrow();
        book2.borrow();
        book3.borrow();
        book4.borrow();
        book5.borrow();

        book1.returnBook();
        book2.returnBook();
        book3.returnBook();
        book4.returnBook();
        book5.returnBook();
    }
}
