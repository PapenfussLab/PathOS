CREATE TABLE mp_detente (
    sample		VARCHAR(20) NOT NULL,
    patient		VARCHAR(100) NOT NULL,
    request_date	VARCHAR(31) NULL,
    collect_date	VARCHAR(31) NULL,
    rcvd_date		VARCHAR(31) NULL,
    auth_date		VARCHAR(31) NULL,
    urn			VARCHAR(30) NOT NULL,
    location		VARCHAR(8) NOT NULL,
    pay_cat             varchar(20) not NULL,
    requester		VARCHAR(100) NOT NULL,
    test_set		VARCHAR(50) NOT NULL,
    test_desc		VARCHAR(50) NOT NULL,
    dob                 VARCHAR(30) NOT NULL,
    sex                 VARCHAR(10) NOT NULL
);
