FROM flyway/flyway:6.0.8

ADD migration /flyway/sql

#Assert that migration files exist
RUN ls /flyway/sql/*sql

CMD ["migrate"]
