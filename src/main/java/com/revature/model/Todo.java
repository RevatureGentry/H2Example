package com.revature.model;

import java.util.Objects;

public class Todo {

	private final int id;
	private final String title;
	private final String body;

	public Todo(int id, String title, String body) {
		this.id = id;
		this.title = title;
		this.body = body;
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getBody() {
		return body;
	}

	@Override
	public int hashCode() {
		return Objects.hash(body, id, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Todo)) {
			return false;
		}
		Todo other = (Todo) obj;
		return Objects.equals(body, other.body) && id == other.id && Objects.equals(title, other.title);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Todo [id=");
		builder.append(id);
		builder.append(", title=");
		builder.append(title);
		builder.append(", body=");
		builder.append(body);
		builder.append("]");
		return builder.toString();
	}

}
