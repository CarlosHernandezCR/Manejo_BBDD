package dao.impl.JDBC;

import common.constants.CredentialConstants;
import common.constants.SQLQueries;
import dao.CredentialsDao;
import common.util.DBConnectionPool;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.Credential;
import model.error.HospitalError;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CredentialsDaoImpl implements CredentialsDao {
    private final DBConnectionPool db;
    @Inject
    public CredentialsDaoImpl(DBConnectionPool dbConnectionPool){
        this.db=dbConnectionPool;
    }
    @Override
    public Either<HospitalError, List<Credential>> getAll() {
        return null;
    }

    public Either<HospitalError, Credential> get(String username) {
        Either<HospitalError, Credential> result;
        try (Connection con = db.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(SQLQueries.GETCREDENTIALS)) {
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("idCredential");
                Integer idPatient = rs.getInt("idPatient");
                String name = rs.getString("username");
                String passw = rs.getString("password");
                if(idPatient==null){
                    result = Either.right(new Credential(id,0, name, passw));
                }else
                    result = Either.right(new Credential(id,idPatient, name, passw));
            }else {
                result = Either.left(new HospitalError(0,CredentialConstants.NOT_FOUND));
            }
        } catch (SQLException e) {
            result = Either.left(new HospitalError(e.getErrorCode(),CredentialConstants.ERROR_READING_FILE));
        }
        return result;
    }

    @Override
    public Either<HospitalError, Integer> add(Credential o) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> delete(Credential o) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> update(Credential o) {
        return null;
    }
}