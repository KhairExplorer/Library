import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class App extends Application {

    private ArrayList<Book> bookData = new ArrayList<>();

    private TableView<Book> tableView = new TableView<>();

    Label lblOutput = new Label();

    // Start method is required for JavaFX applications
    // This method is called when the application is launched
    @SuppressWarnings("unchecked") // Suppress warnings for unchecked operations
    @Override
    public void start(Stage stage) {

        // Data Fields

        TextField tfDelete = new TextField();
        TextField tfSearch = new TextField();

        TextField tfFilterSearch = new TextField();
        tfFilterSearch.setPromptText("Filter by Title or Author");

        ComboBox<String> cbSearch = new ComboBox<>();
        cbSearch.getItems().addAll("Book ID", "Title");

        // Add a listener to the filter search box to filter the table view
        tfFilterSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchText = newValue.trim().toLowerCase();
            if (searchText.isEmpty()) {
                tableView.getItems().setAll(bookData);
                lblOutput.setText("");
            } else {
                ArrayList<Book> searchResults = new ArrayList<>();
                for (Book book : bookData) {
                    if (book.getTitle().toLowerCase().contains(searchText)
                            || book.getAuthor().toLowerCase().contains(searchText)) {
                        searchResults.add(book);
                    }
                }
                tableView.getItems().setAll(searchResults);
            }
        });

        lblOutput.setStyle("-fx-font-size: 20px;");
        lblOutput.setAlignment(Pos.CENTER);
        lblOutput.getStyleClass().add("bold-label");

        Button btInsert = new Button();
        btInsert.setOnAction(e -> showInsertInterface());

        // Load the image
        Image imageInsert = new Image(getClass().getResource("/Icons/Insert.png").toExternalForm());

        // Create an ImageView
        ImageView imageViewInsert = new ImageView(imageInsert);
        imageViewInsert.setFitWidth(50); // Set width
        imageViewInsert.setFitHeight(50); // Set height

        // Set the ImageView as the graphic for the button
        btInsert.setGraphic(imageViewInsert);

        Button btDelete = new Button();

        // Load the image
        Image imageDelete = new Image(getClass().getResource("/Icons/Delete.png").toExternalForm());

        // Create an ImageView
        ImageView imageViewDelete = new ImageView(imageDelete);
        imageViewDelete.setFitWidth(50); // Set width
        imageViewDelete.setFitHeight(50); // Set height

        // Set the ImageView as the graphic for the button
        btDelete.setGraphic(imageViewDelete);

        Button btSearch = new Button(); // Search by book ID or Title

        // Load the image
        Image imageSearch = new Image("/Icons/Search.png");

        // Create an ImageView
        ImageView imageViewSearch = new ImageView(imageSearch);
        imageViewSearch.setFitWidth(50); // Set width
        imageViewSearch.setFitHeight(50); // Set height

        // Set the ImageView as the graphic for the button
        btSearch.setGraphic(imageViewSearch);

        Button btDisplay = new Button();

        // Load the image
        Image imageDisplay = new Image("Icons/Statistics.png");

        // Create an ImageView
        ImageView imageViewDisplay = new ImageView(imageDisplay);
        imageViewDisplay.setFitWidth(50); // Set width
        imageViewDisplay.setFitHeight(50); // Set height

        // Set the ImageView as the graphic for the button
        btDisplay.setGraphic(imageViewDisplay);

        btDisplay.setOnAction(e -> {
            if (bookData.isEmpty()) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("No data loaded. Please load a file first.");
                alert.showAndWait();
                return;
            }
            showDisplayInterface();
        });

        Button btReset = new Button("Reset");
        btReset.setStyle("-fx-font-size: 20px;");

        btReset.setOnAction(e -> {
            tableView.getItems().setAll(bookData);
            lblOutput.setText("Table view reset to show all books.");
        });

        Button btSave = new Button("Save the Updated Data");
        btSave.setOnAction(e -> saveBooks(stage));

        // Load the image
        Image imageSave = new Image("/Icons/Save.png");

        // Create an ImageView
        ImageView imageViewSave = new ImageView(imageSave);
        imageViewSave.setFitWidth(50); // Set width
        imageViewSave.setFitHeight(50); // Set height

        // Set the ImageView as the graphic for the button
        btSave.setGraphic(imageViewSave);

        Button btLoad = new Button("Load Data from File");
        btLoad.setOnAction(e -> readBooks(stage));

        // Load the image
        Image imageLoad = new Image("/Icons/Load.png");

        // Create an ImageView
        ImageView imageViewLoad = new ImageView(imageLoad);
        imageViewLoad.setFitWidth(50); // Set width
        imageViewLoad.setFitHeight(50); // Set height

        // Set the ImageView as the graphic for the button
        btLoad.setGraphic(imageViewLoad);

        Button btClear = new Button("Clear");
        btClear.setStyle("-fx-font-size: 20px;");
        btClear.setOnAction(e -> {
            tfDelete.clear();
            tfSearch.clear();
            cbSearch.getSelectionModel().clearSelection();
            tfFilterSearch.clear();
            lblOutput.setText("");
        });

        btDelete.setOnAction(e -> {
            try {
                int bookId = Integer.parseInt(tfDelete.getText());
                Book bookToRemove = null;
                for (Book book : bookData) {
                    if (book.getBookId() == bookId) {
                        bookToRemove = book;
                        break;
                    }
                }
                if (bookToRemove != null) {
                    bookData.remove(bookToRemove);
                    tableView.getItems().remove(bookToRemove);
                    lblOutput.setText("Book deleted successfully");
                } else {
                    lblOutput.setText("Book has not founded to delete it");
                }
            } catch (NumberFormatException ex) {
                lblOutput.setText("Invalid input. Please enter a valid BookID");
            }
        });

        btSearch.setOnAction(e -> {
            String searchText = tfSearch.getText().trim();
            String searchCriteria = cbSearch.getValue();
            if (searchText.isEmpty() || searchCriteria == null) {
                lblOutput.setText("Please enter search text and select search criteria.");
                return;
            }

            ArrayList<Book> searchResults = new ArrayList<>();
            for (Book book : bookData) {
                if (searchCriteria.equals("Book ID")) {
                    try {
                        int bookId = Integer.parseInt(searchText);
                        if (book.getBookId() == bookId) {
                            searchResults.add(book);
                        }
                    } catch (NumberFormatException ex) {
                        lblOutput.setText("Invalid Book ID format.");
                        return;
                    }
                } else if (searchCriteria.equals("Title")) {
                    if (book.getTitle().equalsIgnoreCase(searchText)) {
                        searchResults.add(book);
                    }
                }
            }

            if (searchResults.isEmpty()) {
                lblOutput.setText("No books found.");
            } else {
                tableView.getItems().setAll(searchResults);
                lblOutput.setText("Search results displayed.");
            }
        });

        // Let the user edit some fields in the tableview
        tableView.setEditable(true);

        // Set up TableView columns
        TableColumn<Book, Integer> bookIdColumn = new TableColumn<>("BookID");
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        bookIdColumn.setMinWidth(100);

        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.setMinWidth(200);
        titleColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        titleColumn.setOnEditCommit(event -> {
            Book book = event.getRowValue();
            book.setTitle(event.getNewValue());
        });

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorColumn.setMinWidth(200);
        authorColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        authorColumn.setOnEditCommit(event -> {
            Book book = event.getRowValue();
            book.setAuthor(event.getNewValue());
        });

        TableColumn<Book, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryColumn.setMinWidth(150);

        TableColumn<Book, Integer> yearColumn = new TableColumn<>("Published Year");
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("publishedYear"));
        yearColumn.setMinWidth(150);

        TableColumn<Book, String> isbnColumn = new TableColumn<>("ISBN");
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        isbnColumn.setMinWidth(150);

        TableColumn<Book, String> activeColumn = new TableColumn<>("Active Author");
        activeColumn.setCellValueFactory(cellData -> {
            Book book = cellData.getValue();
            boolean isActive = book.getPublishedYear() >= LocalDate.now().getYear() - 5;
            if (isActive) {
                return new SimpleStringProperty("Yes");
            } else {
                return new SimpleStringProperty("No");
            }
        });
        activeColumn.setMinWidth(100);

        tableView.getColumns().addAll(bookIdColumn, titleColumn, authorColumn, categoryColumn, yearColumn, isbnColumn,
                activeColumn);

        HBox hBox2 = new HBox(30);
        hBox2.getChildren().addAll(tfDelete, btDelete);
        hBox2.setAlignment(Pos.CENTER);

        HBox hBox3 = new HBox(30);
        hBox3.getChildren().addAll(tfSearch, cbSearch, btSearch, btReset);
        hBox3.setAlignment(Pos.CENTER);

        HBox hBox5 = new HBox(30);
        hBox5.getChildren().addAll(btClear, btLoad, btSave);
        hBox5.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(tfFilterSearch, tableView);
        vBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(35);
        root.getChildren().addAll(btInsert, hBox2, hBox3, btDisplay, vBox, lblOutput, hBox5);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 1100, 750);
        stage.setScene(scene);
        stage.setTitle("Library Management System");
        stage.show();
    }

    private void showInsertInterface() {
        Stage insertStage = new Stage();
        insertStage.setTitle("Insert New Book");

        TextField tfBookId = new TextField();
        tfBookId.setPromptText("BookID");
        TextField tfTitle = new TextField();
        tfTitle.setPromptText("Title");
        TextField tfAuthor = new TextField();
        tfAuthor.setPromptText("Author");
        TextField tfCategory = new TextField();
        tfCategory.setPromptText("Category");
        TextField tfPublishedYear = new TextField();
        tfPublishedYear.setPromptText("Published Year");
        TextField tfIsbn = new TextField();
        tfIsbn.setPromptText("ISBN");

        Button btSubmit = new Button("Submit");
        btSubmit.setOnAction(e -> {
            try {
                int bookId = Integer.parseInt(tfBookId.getText());
                String title = tfTitle.getText();
                String author = tfAuthor.getText();
                String category = tfCategory.getText();
                int publishedYear = Integer.parseInt(tfPublishedYear.getText());
                String isbn = tfIsbn.getText();

                // Check if the book already exists
                for (Book book : bookData) {
                    if (book.getBookId() == bookId && book.getIsbn().equals(isbn)) {
                        lblOutput.setText("Book with the same BookID and ISBN already exists.");
                        return;
                    }
                    if (book.getBookId() == bookId) {
                        lblOutput.setText("Book with the same BookID already exists.");
                        return;
                    }
                    if (book.getIsbn().equals(isbn)) {
                        lblOutput.setText("Book with the same ISBN already exists.");
                        return;
                    }
                }

                if (publishedYear > 2025 && !isbn.matches("\\d{3}-\\d{10}")) {
                    lblOutput.setText(
                            "Published year cannot be greater than 2025, and ISBN must be in the format 3int-10int.");
                    return;
                }

                if (publishedYear > 2025) {
                    lblOutput.setText("Published year cannot be greater than 2025.");
                    return;
                }

                // Check if the ISBN is in the correct format
                if (!isbn.matches("\\d{3}-\\d{10}")) {
                    lblOutput.setText("Invalid ISBN format. Please enter in the format 3int-10int.");
                    return;
                }

                Book book = new Book(bookId, title, author, category, publishedYear, isbn);
                bookData.add(book);
                tableView.getItems().add(book);
                lblOutput.setText("Book added successfully");
                insertStage.close();
            } catch (NumberFormatException ex) {
                lblOutput.setText("Invalid input. Please enter the data in the correct format.");
            }
        });

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(tfBookId, tfTitle, tfAuthor, tfCategory, tfPublishedYear, tfIsbn, btSubmit);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 400, 300);
        insertStage.setScene(scene);
        insertStage.show();
    }

    private void showDisplayInterface() {
        Stage displayStage = new Stage();
        displayStage.setTitle("Statistics");

        // Calculate statistics
        ArrayList<String> categories = new ArrayList<>();
        ArrayList<Integer> categoryCounts = new ArrayList<>();
        ArrayList<String> authors = new ArrayList<>();
        ArrayList<Integer> authorCounts = new ArrayList<>();
        ArrayList<Integer> years = new ArrayList<>();
        ArrayList<Integer> yearCounts = new ArrayList<>();
        ArrayList<ArrayList<String>> authorBooks = new ArrayList<>();

        for (Book book : bookData) {
            String category = book.getCategory();
            if (categories.contains(category)) {
                int index = categories.indexOf(category);
                categoryCounts.set(index, categoryCounts.get(index) + 1);
            } else {
                categories.add(category);
                categoryCounts.add(1);
            }

            String author = book.getAuthor();
            if (authors.contains(author)) {
                int index = authors.indexOf(author);
                authorCounts.set(index, authorCounts.get(index) + 1);
                authorBooks.get(index).add(book.getTitle());
            } else {
                authors.add(author);
                authorCounts.add(1);
                ArrayList<String> books = new ArrayList<>();
                books.add(book.getTitle());
                authorBooks.add(books);
            }

            int year = book.getPublishedYear();
            if (years.contains(year)) {
                int index = years.indexOf(year);
                yearCounts.set(index, yearCounts.get(index) + 1);
            } else {
                years.add(year);
                yearCounts.add(1);
            }
        }

        // Find the years with the maximum and minimum number of books published
        int maxCount = yearCounts.get(0);
        int minCount = yearCounts.get(0);
        for (int count : yearCounts) {
            if (count > maxCount) {
                maxCount = count;
            }
            if (count < minCount) {
                minCount = count;
            }
        }

        ArrayList<Integer> maxYears = new ArrayList<>();
        ArrayList<Integer> minYears = new ArrayList<>();
        for (int i = 0; i < yearCounts.size(); i++) {
            if (yearCounts.get(i) == maxCount) {
                maxYears.add(years.get(i));
            }
            if (yearCounts.get(i) == minCount) {
                minYears.add(years.get(i));
            }
        }

        // Find the authors with the maximum and minimum number of books published
        int maxAuthorCount = authorCounts.get(0);
        int minAuthorCount = authorCounts.get(0);
        for (int count : authorCounts) {
            if (count > maxAuthorCount) {
                maxAuthorCount = count;
            }
            if (count < minAuthorCount) {
                minAuthorCount = count;
            }
        }

        ArrayList<Integer> maxAuthors = new ArrayList<>();
        ArrayList<Integer> minAuthors = new ArrayList<>();
        for (int i = 0; i < authorCounts.size(); i++) {
            if (authorCounts.get(i) == maxAuthorCount) {
                maxAuthors.add(i);
            }
            if (authorCounts.get(i) == minAuthorCount) {
                minAuthors.add(i);
            }
        }

        // Display statistics in a BorderPane
        BorderPane borderPane = new BorderPane();

        VBox topBox = new VBox(10);
        topBox.setAlignment(Pos.CENTER);
        topBox.getChildren().add(new Label("Number of books by category:"));
        for (int i = 0; i < categories.size(); i++) {
            topBox.getChildren().add(new Label(categories.get(i) + ": " + categoryCounts.get(i)));
        }
        borderPane.setTop(topBox);

        VBox leftBox = new VBox(10);
        leftBox.setAlignment(Pos.CENTER);
        leftBox.getChildren().add(new Label("Number of books by author:"));
        for (int i = 0; i < authors.size(); i++) {
            leftBox.getChildren().add(new Label(authors.get(i) + ": " + authorCounts.get(i)));
        }
        borderPane.setLeft(leftBox);

        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.getChildren().add(new Label("Number of books published in specific year:"));
        for (int i = 0; i < years.size(); i++) {
            centerBox.getChildren().add(new Label(years.get(i) + ": " + yearCounts.get(i)));
        }
        borderPane.setCenter(centerBox);

        VBox rightBox = new VBox(10);
        rightBox.setAlignment(Pos.CENTER);
        rightBox.getChildren().add(new Label("The years with the maximum number of books published:"));
        for (int year : maxYears) {
            rightBox.getChildren().add(new Label(year + " (" + maxCount + " books)"));
        }
        rightBox.getChildren().add(new Label("The years with the minimum number of books published:"));
        for (int year : minYears) {
            rightBox.getChildren().add(new Label(year + " (" + minCount + " books)"));
        }
        borderPane.setRight(rightBox);

        VBox bottomBox = new VBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.getChildren().add(new Label("The authors with the maximum number of books published:"));
        for (int index : maxAuthors) {
            bottomBox.getChildren().add(new Label(authors.get(index) + " (" + authorCounts.get(index) + " books)"));

            TableView<String> maxAuthorTable = new TableView<>();
            TableColumn<String, String> titleColumn = new TableColumn<>("Title");
            titleColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()));
            maxAuthorTable.getColumns().add(titleColumn);
            maxAuthorTable.getItems().addAll(authorBooks.get(index));
            bottomBox.getChildren().add(maxAuthorTable);
        }

        bottomBox.getChildren().add(new Label("The authors with the minimum number of books published:"));
        for (int index : minAuthors) {
            bottomBox.getChildren().add(new Label(authors.get(index) + " (" + authorCounts.get(index) + " books)"));

            TableView<String> minAuthorTable = new TableView<>();
            TableColumn<String, String> titleColumn = new TableColumn<>("Title");
            titleColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()));
            minAuthorTable.getColumns().add(titleColumn);
            minAuthorTable.getItems().addAll(authorBooks.get(index));
            bottomBox.getChildren().add(minAuthorTable);
        }
        borderPane.setBottom(bottomBox);

        ScrollPane scrollPane = new ScrollPane(borderPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Scene scene = new Scene(scrollPane, 800, 600);
        displayStage.setScene(scene);
        displayStage.show();
    }

    // Read the data from the file and populate the bookData ArrayList
    private void readBooks(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a file");
        fileChooser.setInitialDirectory(new File("C:\\Users\\Lenovo\\Desktop"));

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try (Scanner input = new Scanner(file)) {
                if (!file.getName().endsWith(".txt")) {
                    lblOutput.setText("Error: Invalid file format. Please select a .txt file");
                    return;
                }
                while (input.hasNext()) {
                    String line = input.nextLine();
                    String[] parts = line.split(",");

                    if (parts.length == 6) {
                        String bookId = parts[0].trim();
                        int id = Integer.parseInt(bookId);

                        String title = parts[1].trim();
                        String author = parts[2].trim();
                        String category = parts[3].trim();
                        String publishedYear = parts[4].trim();
                        int year = Integer.parseInt(publishedYear);
                        String isbn = parts[5].trim();

                        // Check if the book already exists
                        boolean exists = false;
                        for (Book book : bookData) {
                            if (book.getBookId() == id) {
                                exists = true;
                                break;
                            }
                        }

                        if (!exists) {
                            Book book = new Book(id, title, author, category, year, isbn);
                            bookData.add(book);
                            tableView.getItems().add(book);
                        }
                    } else {
                        lblOutput.setText("Error with input format");
                        return;
                    }
                }
                lblOutput.setText("File has been loaded successfully");
            } catch (FileNotFoundException ex) {
                lblOutput.setText("Error: File not found or cannot be opened.");
            }
        }
    }

    // Save the data to a file
    private void saveBooks(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.setInitialDirectory(new File("C:\\Users\\Lenovo\\Desktop"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (PrintWriter output = new PrintWriter(file)) {
                for (Book book : bookData) {
                    output.println(String.format("%03d", book.getBookId()) + "," + book.getTitle() + ","
                            + book.getAuthor() + "," + book.getCategory() + "," + book.getPublishedYear() + ","
                            + book.getIsbn());
                }
                lblOutput.setText("File has been saved successfully");
            } catch (FileNotFoundException ex) {
                lblOutput.setText("Error: File cannot be saved.");
            }
        }
    }

    // Main method is required for JavaFX applications
    // This method is the entry point of the application
    public static void main(String[] args) {
        launch(args);
    }
}
