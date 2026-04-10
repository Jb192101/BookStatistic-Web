from fastapi import FastAPI

app = FastAPI()

@app.get("/analyze")
def read_root():
    return None

@app.get("/similarity")
def get_similarity(book_dto):
    return None

@app.post("/texts")
def add_new_text(bookId, text):
    return None

@app.post("/books")
def add_new_book(book):
    return None