export interface User {
  id: string;
  name: string;
  email: string;
  enabled: boolean;
  createdAt: string | null;
  roles: string[];
}

export interface CreateUserRequest {
  name: string;
  email: string;
  password: string;
}

export interface UpdateUserRequest {
  name: string;
  email: string;
  password?: string;
  enabled: boolean;
  roles: string[];
}