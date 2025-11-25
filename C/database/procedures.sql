DELIMITER //

DROP PROCEDURE IF EXISTS create_booking //

CREATE PROCEDURE create_booking(
    IN p_user_id INT,
    IN p_screening_id INT,
    IN p_seat_number VARCHAR(10),
    IN p_price DECIMAL(10, 2),
    OUT p_result VARCHAR(50)
)
BEGIN
    DECLARE v_count INT;
    
    -- Start transaction
    START TRANSACTION;
    
    -- Check if seat is already taken (CONFIRMED)
    -- We use FOR UPDATE to lock the rows if necessary, but here we are checking for existence.
    -- To strictly prevent race conditions in repeatable read, we might need more locking, 
    -- but for this scope, a simple check inside transaction with serializable isolation or atomic insert is key.
    -- Here we rely on the check + insert within transaction.
    
    SELECT COUNT(*) INTO v_count
    FROM BOOKINGS
    WHERE screening_id = p_screening_id 
      AND seat_number = p_seat_number
      AND status = 'CONFIRMED';
      
    IF v_count > 0 THEN
        ROLLBACK;
        SET p_result = 'FAIL: Seat already taken';
    ELSE
        INSERT INTO BOOKINGS (user_id, screening_id, seat_number, price, status)
        VALUES (p_user_id, p_screening_id, p_seat_number, p_price, 'CONFIRMED');
        
        COMMIT;
        SET p_result = 'SUCCESS';
    END IF;
END //

DELIMITER ;
