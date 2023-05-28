package org.persistence.database;

import org.model.Boss;
import org.persistence.repository.BossRepository;
import org.persistence.utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BossDataBaseRepository implements BossRepository {

    private final JdbcUtils jdbcUtils = new JdbcUtils();

    @Override
    public Boss save(Boss boss) {
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO boss(firstname, lastname, email, password) VALUES(?, ?, ?, ?)")) {
            preparedStatement.setString(1, boss.getFirstName());
            preparedStatement.setString(2, boss.getLastName());
            preparedStatement.setString(3, boss.getEmail());
            preparedStatement.setString(4, boss.getPassword());
            int rowsNumber = preparedStatement.executeUpdate();
            if (rowsNumber > 0) {
                return boss;
            }
        } catch (SQLException ex) {
            System.err.println("Error DB " + ex);
        }
        return null;
    }

    @Override
    public Boss delete(Long idBoss) {
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM boss WHERE idBoss = ?")) {
            preparedStatement.setLong(1, idBoss);
            int rowsNumber = preparedStatement.executeUpdate();
            if (rowsNumber > 0) {
                Boss boss = new Boss();
                boss.setId(idBoss);
                boss.setEmail("");
                boss.setPassword("");
                boss.setFirstName("");
                boss.setLastName("");
                return boss;
            }
        } catch (SQLException ex) {
            System.err.println("Error DB " + ex);
        }
        return null;
    }

    @Override
    public Boss update(Boss boss) {
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE boss SET firstname = ?, SET lastname = ?, SET email = ?, SET password = ? WHERE idboss = ?")) {
            preparedStatement.setString(1, boss.getFirstName());
            preparedStatement.setString(2, boss.getLastName());
            preparedStatement.setString(3, boss.getEmail());
            preparedStatement.setString(4, boss.getPassword());
            preparedStatement.setLong(5, boss.getId());
            int rowsNumber = preparedStatement.executeUpdate();
            if (rowsNumber > 0) {
                return boss;
            }
        } catch (SQLException ex) {
            System.err.println("Error DB " + ex);
        }
        return null;
    }

    @Override
    public Boss findOne(Long idBoss) {
        Connection connection = jdbcUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM boss WHERE idboss = ?")){
            preparedStatement.setLong(1, idBoss);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()){
                    return getBossFromDataBase(resultSet);
                }
            }
        }catch (SQLException ex){
            System.err.println("Error DB " + ex);
        }
        return null;
    }

    @Override
    public Iterable<Boss> findAll() {
        Connection connection = jdbcUtils.getConnection();
        List<Boss> bosses = new ArrayList<>();
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM boss")){
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()){
                    Boss boss = getBossFromDataBase(resultSet);
                    bosses.add(boss);
                }
            }
        }catch (SQLException ex){
            System.err.println("Error DB " + ex);
        }
        return bosses;
    }

    @Override
    public long checkBossAccountExistence(String email, String password) {
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT idBoss FROM boss WHERE email = ? AND password = ?")) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error DB " + ex);
        }
        return 0;
    }

    private Boss getBossFromDataBase(ResultSet resultSet) throws SQLException {
        long idB = resultSet.getLong(1);
        String firstname = resultSet.getString(2);
        String lastname = resultSet.getString(3);
        String email = resultSet.getString(4);
        String password = resultSet.getString(5);
        Boss boss = new Boss();
        boss.setId(idB);
        boss.setFirstName(firstname);
        boss.setLastName(lastname);
        boss.setEmail(email);
        boss.setPassword(password);
        return boss;
    }

}
