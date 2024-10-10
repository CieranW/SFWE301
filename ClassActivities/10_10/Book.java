public class Book {
    private String title;
    private String author;
    private boolean isBorrowed;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.isBorrowed = false;
    }

    public Book() {
        this.title = "No Title";
        this.author = "No Author";
        this.isBorrowed = false;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public boolean getIsBorrowed() {
        return isBorrowed;
    }

    public void borrowBook() {
        isBorrowed = true;
    }

    public void borrow() {
        if (!isBorrowed) {
            isBorrowed = true;
            System.out.println("You have borrowed " + title + " by " + author);
        }
        else {
            System.out.println("Sorry, " + title + " by " + author + " is already borrowed.");
        }
    }

    public void returnBook() {
        if (isBorrowed) {
            isBorrowed = false;
            System.out.println("You have returned " + title + " by " + author);
        }
        else {
            System.out.println("Sorry, " + title + " by " + author + " is not borrowed.");
        }
    }

}