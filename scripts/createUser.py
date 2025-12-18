#!/usr/bin/env python3
import argparse
from argon2 import PasswordHasher
import psycopg

p = argparse.ArgumentParser()
_ = p.add_argument("dbconnect", help="Database connect string of this format: 'dbname=XX user=XX password=XX")
_ = p.add_argument("schema", help="Database schema name")
_ = p.add_argument("username", help="The new user's username.")
_ = p.add_argument("password", help="The new user's password- hashed via argon2.")
_ = p.add_argument("roles", help="Comma-separated list of roles.")
args = p.parse_args()

print("Hashing password...")
p = args.password
ph = PasswordHasher()
passwordHash = ph.hash(p)

print("Connecting to database...")
with psycopg.connect(conninfo=args.dbconnect, options=f"-c search_path={args.schema}") as conn:
    with conn.cursor() as c:
        c.execute("""
            INSERT INTO users (username, password_hash, roles) 
            VALUES (%s, %s, %s);
        """, (args.username, passwordHash, args.roles))
        conn.commit()
print(f"Success! Created {args.username}")
