const streamErr = e => {
    console.warn("error");
    console.warn(e);
}

function showAuthors() {
    console.log("showAuthors .................");
    fetch("/api/v1/author").then((response) => {
        return can.ndjsonStream(response.body);
    }).then(dataStream => {
        const reader = dataStream.getReader();
        const read = result => {
            if (result.done) {
                return;
            }
            console.log('render ....', result.value);
            outputAuthors(result.value);
            reader.read().then(read, streamErr);
        }
        reader.read().then(read, streamErr);
    });
}

function showGenres() {
    console.log("showGenres .................");
    fetch("/api/v1/genre").then((response) => {
        return can.ndjsonStream(response.body);
    }).then(dataStream => {
        const reader = dataStream.getReader();
        const read = result => {
            if (result.done) {
                return;
            }
            console.log('render ....', result.value);
            outputGenres(result.value);
            reader.read().then(read, streamErr);
        }
        reader.read().then(read, streamErr);
    });
}

function showBooks() {
    console.log("showBooks .................");
    fetch("/api/v1/book").then((response) => {
        return can.ndjsonStream(response.body);
    }).then(dataStream => {
        const reader = dataStream.getReader();
        const read = result => {
            if (result.done) {
                return;
            }
            console.log('render ....', result.value);
            outputBooks(result.value);
            reader.read().then(read, streamErr);
        }
        reader.read().then(read, streamErr);
    });
}

function comboAuthors() {
    console.log("comboAuthors .................");
    fetch("/api/v1/author").then((response) => {
        return can.ndjsonStream(response.body);
    }).then(dataStream => {
        const reader = dataStream.getReader();
        const read = result => {
            if (result.done) {
                return;
            }
            console.log('render authors....', result.value);
            outputComboAuthors(result.value);
            reader.read().then(read, streamErr);
        }
        reader.read().then(read, streamErr);
    });
}

function outputComboAuthors(authors) {
    console.log("outputComboAuthors", authors);
    let select = document.getElementById("authorId");

    authors.forEach(author => {
        let option = document.createElement("option");
        option.value = author.id;
        option.text = author.fullName;
        select.append(option);
    });
}

function comboGenres() {
    console.log("comboGenres .................");

    fetch("/api/v1/genre").then((response) => {
        return can.ndjsonStream(response.body);
    }).then(dataStream => {
        const reader = dataStream.getReader();
        const read = result => {
            if (result.done) {
                return;
            }
            console.log('render genres....', result.value);
            outputComboGenres(result.value);
            reader.read().then(read, streamErr);
        }
        reader.read().then(read, streamErr);
    });
}

function outputComboGenres(genres) {
    console.log("outputComboGenres", genres);
    let select = document.getElementById("genreId");

    genres.forEach(genre => {
        let option = document.createElement("option");
        option.value = genre.id;
        option.text = genre.name;
        select.append(option);
    });
}

function outputBooks(books) {
    console.log("outputBooks", books);
    let table = document.getElementById("table-items");
    books.forEach(book => {
        let row = document.createElement("tr");
        row.setAttribute("id", book.id);
        row.innerHTML = `
            <td>${book.id}</td>
            <td>${book.title}</td>
            <td>${book.genreTitles}</td>
            <td>${book.authorTitle}</td>
            <td><a href="/book/edit?id=${book.id}">Изменить</a></td>
            <td><button onclick="deleteBook('${book.id}')" class="link">Удалить</button></td>
        `;
        table.append(row);
    });
}

function outputAuthors(authors) {
    console.log("outputAuthors", authors);
    let table = document.getElementById("table-items");
    authors.forEach(author => {
        let row = document.createElement("tr");
        row.setAttribute("id", author.id);
        row.innerHTML = `
            <td>${author.id}</td>
            <td>${author.fullName}</td>
            <td><a href="/author/edit?id=${author.id}">Изменить</a></td>
            <td><button onclick="deleteAuthor('${author.id}')" class="link">Удалить</button></td>
        `;
        table.append(row);
    });
}

function outputGenres(genres) {
    console.log("outputGenres", genres);
    let table = document.getElementById("table-items");
    genres.forEach(genre => {
        let row = document.createElement("tr");
        row.setAttribute("id", genre.id);
        row.innerHTML = `
            <td>${genre.id}</td>
            <td>${genre.name}</td>
            <td><a href="/genre/edit?id=${genre.id}">Изменить</a></td>
            <td><button onclick="deleteGenre('${genre.id}')" class="link">Удалить</button></td>
        `;
        table.append(row);
    });
}

function deleteAuthor(id) {
    console.log('deleteAuthor ....................', id);
    fetch(`api/v1/author/${id}`, {
        method: 'DELETE',
    })
        .then(() => {
            console.log(`removed author ${id}`);
            document.getElementById(id).remove();
        })
        .catch(error => console.error("Ошибка удаления автора ", error));
}

function deleteGenre(id) {
    console.log('deleteGenre ....................', id);
    fetch(`api/v1/genre/${id}`, {
        method: 'DELETE',
    })
        .then(() => {
            console.log(`removed genre ${id}`);
            document.getElementById(id).remove();
        })
        .catch(error => console.error("Ошибка удаления жанра ", error));
}


function deleteBook(id) {
    console.log('deleteBook ....................', id);
    fetch(`api/v1/book/${id}`, {
        method: 'DELETE',
    })
        .then(() => {
            console.log(`removed book ${id}`);
            document.getElementById(id).remove();
        })
        .catch(error => console.error("Ошибка удаления книги ", error));
}

function saveAuthor() {
    console.log('saveAuthor ................');

    const id = document.getElementById("id-input").value;
    const fullName = document.getElementById("author-name-input").value;
    const method = id ? "PUT" : "POST";

    const author = {
        id: id,
        fullName: fullName
    };

    fetch("/api/v1/author", {
        method: method,
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(author)
    })
        .then(response => {
            console.log('response', response.json());
            if (response.ok) {
                window.location.href = "/author";
            }
        })
        .catch(error => {
            console.log("Fetch ошибка сохранения автора", error)
        });
}

function saveGenre() {
    console.log('saveGenre ................');

    const id = document.getElementById("id-input").value;
    const name = document.getElementById("genre-name-input").value;
    const method = id ? "PUT" : "POST";

    const genre = {
        id: id,
        name: name
    };

    fetch("/api/v1/genre", {
        method: method,
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(genre)
    })
        .then(response => {
            console.log('response', response.json());
            if (response.ok) {
                window.location.href = "/genre";
            }
        })
        .catch(error => {
            console.log("Fetch ошибка сохранения жанра", error)
        });
}

function saveBook() {
    console.log('saveBook ................');

    const id = document.getElementById("id-input").value;
    const name = document.getElementById("book-name-input").value;
    const method = id ? "PUT" : "POST";


    const author = document.getElementById("authorId");
    const authorDto = {
        id: author.value,
        fullName: author.options[author.selectedIndex].text
    };

    console.log('authorDto', authorDto);

    const genres = document.getElementById("genreId").options;
    const genreIds = Array.from(genres)
        .filter(genre => genre.selected)
        .map(item => item.value);

    console.log('genreIds', genreIds);

    const book = {
        id: id,
        title: name,
        authorDto: authorDto,
        genreDtos: genreIds
    };

    console.log('book', book);

    fetch("/api/v1/book", {
        method: method,
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(book)
    })
        .then(response => {
            console.log('response', response.json());
            if (response.ok) {
                window.location.href = "/";
            }
        })
        .catch(error => {
            console.log("Fetch ошибка сохранения книги", error)
        });
}

function loadBook() {

    (async () => {

        await comboGenres();
        await comboAuthors();

        const id = document.getElementById("id-input").value;
        console.log("Загрузка книги c id --> ", id);
        if (!id) {
            return;
        }

        fetch(`/api/v1/book/${id}`, {
            method: "GET",
        })
            .then(response => response.json())
            .then(book => {
                console.log('Получена книга', book);

                document.getElementById("authorId").value = book.authorDto.id;
                const genres = document.getElementById("genreId")
                const genreIds = book.genreDtos;

                console.log('genreIds', genreIds);
                console.log('genres.options.length', genres.options.length);

                for (let i = 0; i < genres.options.length; i++) {
                    let optionValue = genres.options[i].value;
                    genres.options[i].selected = genreIds.indexOf(optionValue) >= 0;
                }

                document.getElementById('book-name-input').value = book.title;
            })
            .catch(error => console.error("Ошибка загрузки книги по id ", error));
    })()
}

function loadAuthor() {
    const id = document.getElementById("id-input").value;
    console.log("Загрузка автора c id --> ", id);
    if (!id) {
        return;
    }

    fetch(`/api/v1/author/${id}`).then((response) => {
        return can.ndjsonStream(response.body);
    }).then(dataStream => {
        const reader = dataStream.getReader();
        const read = result => {
            if (result.done) {
                return;
            }
            var author = result.value;
            console.log('render ....', author);
            document.getElementById('author-name-input').value = author.fullName;
            reader.read().then(read, streamErr);
        }
        reader.read().then(read, streamErr);
    });
}

function loadGenre() {
    const id = document.getElementById("id-input").value;
    console.log("Загрузка жанра c id --> ", id);
    if (!id) {
        return;
    }

    fetch(`/api/v1/genre/${id}`).then((response) => {
        return can.ndjsonStream(response.body);
    }).then(dataStream => {
        const reader = dataStream.getReader();
        const read = result => {
            if (result.done) {
                return;
            }
            var genre = result.value;
            console.log('render ....', genre);
            document.getElementById('genre-name-input').value = genre.name;
            reader.read().then(read, streamErr);
        }
        reader.read().then(read, streamErr);
    });

}