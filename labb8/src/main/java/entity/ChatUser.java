package entity;

// Класс ChatUser представляет пользователя чата и содержит его данные.
public class ChatUser {
	// Имя пользователя.
	private String name;
	// Время последнего взаимодействия с чатом в миллисекундах.
	private long lastInteractionTime;
	// Идентификатор сессии пользователя.
	private String sessionId;

	// Конструктор класса, инициализирующий объект ChatUser.
	public ChatUser(String name, long lastInteractionTime, String sessionId) {
		super(); // Вызов конструктора родительского класса (в данном случае Object).
		this.name = name; // Установка имени пользователя.
		this.lastInteractionTime = lastInteractionTime; // Установка времени последнего взаимодействия.
		this.sessionId = sessionId; // Установка идентификатора сессии.
	}

	// Метод для получения имени пользователя.
	public String getName() {
		return name;
	}

	// Метод для установки нового имени пользователя.
	public void setName(String name) {
		this.name = name;
	}

	// Метод для получения времени последнего взаимодействия пользователя с чатом.
	public long getLastInteractionTime() {
		return lastInteractionTime;
	}

	// Метод для установки времени последнего взаимодействия пользователя с чатом.
	public void setLastInteractionTime(long lastInteractionTime) {
		this.lastInteractionTime = lastInteractionTime;
	}

	// Метод для получения идентификатора сессии пользователя.
	public String getSessionId() {
		return sessionId;
	}

	// Метод для установки идентификатора сессии пользователя.
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
