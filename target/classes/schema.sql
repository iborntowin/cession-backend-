-- Create sequence for client numbers
CREATE SEQUENCE IF NOT EXISTS client_number_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 1;

-- Create clients table with sequence for client_number
CREATE TABLE IF NOT EXISTS clients (
    id UUID PRIMARY KEY,
    client_number INTEGER DEFAULT nextval('client_number_seq'),
    full_name VARCHAR(255) NOT NULL,
    cin INTEGER NOT NULL UNIQUE CHECK (cin >= 10000000 AND cin <= 99999999),
    job VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on cin column
CREATE INDEX IF NOT EXISTS idx_clients_cin ON clients(cin); 