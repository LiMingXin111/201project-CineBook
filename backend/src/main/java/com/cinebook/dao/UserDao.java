package com.cinebook.dao;

import com.cinebook.db.Database;
import com.cinebook.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    public List<User> getAll() {
        String sql = "SELECT id, username, password_hash, email, role FROM users ORDER BY id";
        List<User> users = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(mapRow(resultSet));
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load users", e);
        }
        return users;
    }

    public User getById(int id) {
        String sql = "SELECT id, username, password_hash, email, role FROM users WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load user by id", e);
        }
        return null;
    }

    public User getByUsername(String username) {
        String sql = "SELECT id, username, password_hash, email, role FROM users WHERE username = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load user by username", e);
        }
        return null;
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to check username", e);
        }
    }

    public User insert(User user) {
        String sql = "INSERT INTO users (username, password_hash, email, role) VALUES (?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getRole());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getInt(1));
                }
            }
            return user;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to insert user", e);
        }
    }

    private User mapRow(ResultSet resultSet) throws Exception {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password_hash"));
        user.setEmail(resultSet.getString("email"));
        user.setRole(resultSet.getString("role"));
        return user;
    }
}
