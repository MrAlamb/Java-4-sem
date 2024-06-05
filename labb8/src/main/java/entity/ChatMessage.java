package entity;

// Класс ChatMessage представляет сообщение в чате.
public class ChatMessage {
	// Текст сообщения.
	private String message;
	// Автор сообщения, объект класса ChatUser.
	private ChatUser author;
	// Временная метка сообщения в миллисекундах.
	private long timestamp;
	// Имя пользователя, для которого сообщение является приватным (если применимо).
	private String privatem;

	// Конструктор класса, инициализирующий объект ChatMessage.
	public ChatMessage(String message, ChatUser author, long timestamp, String privatem) {
		super(); // Вызов конструктора родительского класса (в данном случае Object).
		this.message = message; // Установка текста сообщения.
		this.author = author; // Установка автора сообщения.
		this.timestamp = timestamp; // Установка временной метки сообщения.
		this.privatem = privatem; // Установка имени пользователя для приватного сообщения.
	}

	// Метод для получения текста сообщения.
	public String getMessage() {
		return message;
	}

	// Метод для установки текста сообщения.
	public void setMessage(String message) {
		this.message = message;
	}

	// Метод для получения автора сообщения.
	public ChatUser getAuthor() {
		return author;
	}

	// Метод для установки автора сообщения.
	public void setAuthor(ChatUser author) {
		this.author = author;
	}

	// Метод для получения временной метки сообщения.
	public long getTimestamp() {
		return timestamp;
	}

	// Метод для установки временной метки сообщения.
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	// Метод для получения имени пользователя, для которого сообщение является приватным.
	public String getPrivatem() {
		return privatem;
	}

	// Метод для установки имени пользователя, для которого сообщение является приватным.
	public void setPrivatem(String privatem) {
		this.privatem = privatem;
	}
}
