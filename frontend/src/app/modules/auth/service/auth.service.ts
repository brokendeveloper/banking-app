import { api } from '@/common/api';
import { LoginRequest, LoginResponse } from '../dto/login.dto';
import { RegisterRequest, RegisterResponse } from '../dto/register.dto';

export const authService = {
    login: async (credentials : LoginRequest): Promise<LoginResponse> => {
        try{
            const response = await api.post<LoginResponse>('/auth/login', credentials);
            return response.data;
        } catch (error) {
            console.error('Login failed:', error);
            throw error;
        }
    },

    register: async (credentials: RegisterRequest): Promise<RegisterResponse> => {
        try {
            const response = await api.post<RegisterResponse>('/auth/register', credentials);
            return response.data;
        } catch (error) {
            console.error('Registration failed:', error);
            throw error;
        }
    }
};