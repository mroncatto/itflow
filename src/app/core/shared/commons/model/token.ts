export interface IToken {
    aud: string
    exp: number
    iat: number
    iss: string
    roles: string[]
    sub: string
}