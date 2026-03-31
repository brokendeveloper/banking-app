export interface RegisterRequest {
    name: string;
    email: string;
    password: string;
    cpf: string;
    telephone: string;
}

export interface RegisterResponse {
    name: string;
    email: string;
    message: string;
}