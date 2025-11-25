CREATE TABLE IF NOT EXISTS BOOKINGS (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    screening_id INT NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED', -- CONFIRMED, CANCELLED
    booking_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    price DECIMAL(10, 2) NOT NULL
);

-- Index to help with availability checks
CREATE INDEX idx_screening_seat ON BOOKINGS(screening_id, seat_number);
CREATE INDEX idx_user_bookings ON BOOKINGS(user_id);
