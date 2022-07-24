import { IUser } from "./user"

export interface IAuthResponse {
    access_token: string
    expire: number
    user: IUser
}