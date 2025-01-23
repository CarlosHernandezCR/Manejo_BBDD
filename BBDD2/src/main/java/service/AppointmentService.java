package service;

import dao.AppointmentDao;
import dao.impl.hibernate.AppointmentDaoImpl;
import dao.impl.hibernate.QueriesHibernate;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.error.HospitalError;

import java.time.LocalDate;

public class AppointmentService {
    AppointmentDao appointmentDao;
    QueriesHibernate queriesHibernate;

    @Inject
    public AppointmentService(AppointmentDaoImpl appointmentDao, QueriesHibernate queriesHibernate) {
        this.appointmentDao = appointmentDao;
        this.queriesHibernate = queriesHibernate;
    }

    public Either<HospitalError, LocalDate> getDateWithMorePatients() {
        return queriesHibernate.getAppointmentWithMostPatients();
    }
}
