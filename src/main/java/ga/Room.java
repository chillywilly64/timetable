package ga;

import java.util.Objects;

/**
 * Simple Room abstraction -- used to store the room capacity and compare against the student Group's size.
 */
public class Room {
	private int roomId;
	private final String roomNumber;
	private final int capacity;

	/**
	 * Initialize new Room
	 * 
	 * @param roomId
	 *            The ID for this classroom
	 * @param roomNumber
	 *            The room number
	 * @param capacity
	 *            The room capacity
	 */
	public Room(int roomId, String roomNumber, int capacity) {
		this.roomId = roomId;
		this.roomNumber = roomNumber;
		this.capacity = capacity;
	}

	/**
	 * Initialize new Room
	 *
	 * @param roomNumber
	 *            The room number
	 * @param capacity
	 *            The room capacity
	 */
	public Room(String roomNumber, int capacity) {
		this.roomNumber = roomNumber;
		this.capacity = capacity;
	}

	/**
	 * Return roomId
	 * 
	 * @return roomId
	 */
	public int getRoomId() {
		return this.roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	/**
	 * Return room number
	 * 
	 * @return roomNumber
	 */
	public String getRoomNumber() {
		return this.roomNumber;
	}

	/**
	 * Return room capacity
	 * 
	 * @return capacity
	 */
	public int getRoomCapacity() {
		return this.capacity;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Room)) return false;
		Room room = (Room) o;
		return roomId == room.roomId &&
				capacity == room.capacity &&
				Objects.equals(roomNumber, room.roomNumber);
	}

	@Override
	public int hashCode() {

		return Objects.hash(roomId, roomNumber, capacity);
	}
}