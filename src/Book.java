public class Book {
    private int bookId;
    private String title;
    private String author;
    private String category;
    private int publishedYear;
    private String isbn;

    public Book(int bookId, String title, String author, String category, int publishedYear, String isbn) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.publishedYear = publishedYear;
        this.isbn = isbn;
    }

    // Getters and Setters

    public int getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public int getPublishedYear() {
        return publishedYear;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPublishedYear(int publishedYear) {
        this.publishedYear = publishedYear;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    // toString method
    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", publishedYear=" + publishedYear +
                ", isbn='" + isbn + '\'' +
                '}';
    }

}