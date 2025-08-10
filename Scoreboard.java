import java.sql.*;

public class Scoreboard {
    private Connection conn;
    
    public Scoreboard() {
        try {
            Class.forName("org.sqlite.JDBC");
            
            conn = DriverManager.getConnection("jdbc:sqlite:quiz_scores.db");
            
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS scores (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "player_name TEXT NOT NULL, " +
                "score INTEGER NOT NULL, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
            stmt.close();
        } catch (Exception e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
    
    public void addScore(String playerName, int score) {
        try {
            PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO scores (player_name, score) VALUES (?, ?)"
            );
            pstmt.setString(1, playerName);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error adding score: " + e.getMessage());
        }
    }
    
    public String getHighScores() {
        StringBuilder sb = new StringBuilder();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT player_name, score FROM scores " +
                "ORDER BY score DESC LIMIT 10"
            );
            
            sb.append("Top 10 High Scores:\n");
            sb.append("------------------\n");
            
            int rank = 1;
            while (rs.next()) {
                sb.append(rank).append(". ")
                  .append(rs.getString("player_name")).append(" - ")
                  .append(rs.getInt("score")).append("\n");
                rank++;
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error getting scores: " + e.getMessage());
            return "Error loading high scores.";
        }
        return sb.toString();
    }
    
    public void close() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database: " + e.getMessage());
        }
    }
}