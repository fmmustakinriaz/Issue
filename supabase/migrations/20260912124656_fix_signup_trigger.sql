/*
# Fix: Signup trigger causing "Database error saving new user"

## Problem
The `handle_new_user()` trigger function had a mutable `search_path`, which can
cause it to fail when Supabase's auth system calls it during user creation.
This resulted in a "Database error saving new user" error on signup.

## Fix
1. Recreate `handle_new_user()` with an explicit `SET search_path = public` clause.
2. Revoke EXECUTE on the function from `anon` and `authenticated` roles — only
   the database trigger should call it, not API clients.
3. Recreate `update_updated_at()` with the same search_path fix.
*/

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
DROP FUNCTION IF EXISTS handle_new_user();

CREATE OR REPLACE FUNCTION handle_new_user()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
BEGIN
  INSERT INTO profiles (id, full_name, role)
  VALUES (
    NEW.id,
    COALESCE(NEW.raw_user_meta_data->>'full_name', 'New User'),
    COALESCE(NEW.raw_user_meta_data->>'role', 'student')
  )
  ON CONFLICT (id) DO NOTHING;
  RETURN NEW;
END;
$$;

CREATE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW
  EXECUTE FUNCTION handle_new_user();

REVOKE EXECUTE ON FUNCTION handle_new_user() FROM anon, authenticated;

-- Also fix the update_updated_at function's search_path
DROP TRIGGER IF EXISTS issues_updated_at ON issues;
DROP FUNCTION IF EXISTS update_updated_at();

CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$;

CREATE TRIGGER issues_updated_at
  BEFORE UPDATE ON issues
  FOR EACH ROW
  EXECUTE FUNCTION update_updated_at();