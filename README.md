# Restaurant Management System API Documentation
Version: 1.0  
Last Updated: January 14, 2025

## Table of Contents
1. [Account Service (Port 8080)](#1-account-service-port-8080)
2. [User Information Service (Port 8081)](#2-user-information-service-port-8081)
3. [Food Management Service (Port 8083)](#3-food-management-service-port-8083)
4. [Table Management Service (Port 8084)](#4-table-management-service-port-8084)
5. [Bill Management Service (Port 8086)](#5-bill-management-service-port-8086)

---

# 1. Account Service (Port 8080)
Base URL: `http://localhost:8080/api/v1`

## 1.1. Đăng nhập
**Endpoint**: `/login`  
**Method**: POST  
**Headers**: 
- Content-Type: application/json

**Request Body**:
```json
{
    "username": "string",
    "password": "string"
}
```

**Success Response**:
- Status: 200 OK
- Content: JWT token string

**Error Response**:
- Status: 401 UNAUTHORIZED
- Content: "Sai username hoặc password."

## 1.2. Đăng ký tài khoản
**Endpoint**: `/register`  
**Method**: POST  
**Headers**:
- Content-Type: application/json

**Request Body**:
```json
{
    "username": "string",
    "password": "string",
    "accountType": "string"
}
```

**Success Response**:
- Status: 200 OK
- Content: "Đăng ký thành công"

**Error Response**:
- Content: "Tài khoản đã tồn tại"

## 1.3. Lấy Account ID
**Endpoint**: `/get-accountID`  
**Method**: GET  
**Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
- Content: Account ID (number)

**Error Response**:
- Status: 401 UNAUTHORIZED
- Content: "Token không hợp lệ"

## 1.4. Lấy Account Type
**Endpoint**: `/auth/get-accountType`  
**Method**: GET  
**Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
- Content: Account Type (string)

**Error Response**:
- Status: 401 UNAUTHORIZED
- Content: "Token không hợp lệ"

---

# 2. User Information Service (Port 8081)
Base URL: `http://localhost:8081/api/v1/infoUser`

## 2.1. Lấy tất cả thông tin người dùng
**Endpoint**: `/`  
**Method**: GET  
**Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
- Content: Array of InfoUser objects
```json
[
    {
        "userInfoId": "number",
        "accountId": "number",
        "fullName": "string",
        "sex": "string",
        "email": "string",
        "phoneNumber": "string"
    }
]
```

## 2.2. Lấy thông tin người dùng theo ID
**Endpoint**: `/info`  
**Method**: GET  
**Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
- Content: InfoUser object

## 2.3. Thêm/Cập nhật thông tin người dùng
**Endpoint**: `/add`  
**Method**: POST  
**Headers**:
- Authorization: Bearer {token}
- Content-Type: application/json

**Request Body**:
```json
{
    "fullName": "string",
    "sex": "string",
    "email": "string",
    "phoneNumber": "string"
}
```

---

# 3. Food Management Service (Port 8083)
Base URL: `http://localhost:8083/api/v1`

## 3.1. Danh Mục Món Ăn (Category)
Base path: `/dmFood`

### 3.1.1. Lấy tất cả danh mục
**Endpoint**: `/`  
**Method**: GET  
**Headers**:
- Authorization: Bearer {token}

**Success Response**:
```json
[
    {
        "dmFoodId": "number",
        "categoryName": "string"
    }
]
```

### 3.1.2. Thêm danh mục mới
**Endpoint**: `/add`  
**Method**: POST  
**Request Body**:
```json
{
    "categoryName": "string"
}
```

### 3.1.3. Xóa danh mục
**Endpoint**: `/{id}`  
**Method**: DELETE

## 3.2. Món Ăn (Food Items)
Base path: `/food`

### 3.2.1. Lấy tất cả món ăn
**Endpoint**: `/`  
**Method**: GET

### 3.2.2. Thêm món ăn mới
**Endpoint**: `/add`  
**Method**: POST  
**Request Body**:
```json
{
    "foodName": "string",
    "dmFoodID": "number",
    "price": "string",
    "avtFood": "byte[]"
}
```

### 3.2.3. Cập nhật món ăn
**Endpoint**: `/update/{foodId}`  
**Method**: PUT

### 3.2.4. Xóa món ăn
**Endpoint**: `/{foodID}`  
**Method**: DELETE

---

# 4. Table Management Service (Port 8084)
Base URL: `http://localhost:8084/api/v1/tables`

## 4.1. Tạo bàn mới
**Endpoint**: `/`  
**Method**: POST  
**Request Body**:
```json
{
    "tableName": "string",
    "status": "string"
}
```

## 4.2. Lấy danh sách bàn
**Endpoint**: `/`  
**Method**: GET

## 4.3. Cập nhật trạng thái bàn
**Endpoint**: `/{id}`  
**Method**: PUT  
**Request Body**: Status string

## 4.4. Xóa bàn
**Endpoint**: `/{id}`  
**Method**: DELETE

---

# 5. Bill Management Service (Port 8086)
Base URL: `http://localhost:8086/api/v1`

## 5.1. Quản lý hóa đơn (Bills)
Base path: `/bills`

### 5.1.1. Tạo hóa đơn mới
**Endpoint**: `/`  
**Method**: POST  
**Request Body**:
```json
{
    "tableID": "number"
}
```

### 5.1.2. Lấy hóa đơn theo ngày
**Endpoint**: `/paid-bills?date=yyyy-MM-dd`  
**Method**: GET

### 5.1.3. Thanh toán hóa đơn
**Endpoint**: `/pay/{id}`  
**Method**: PUT

### 5.1.4. Hủy hóa đơn
**Endpoint**: `/{id}/cancel`  
**Method**: PUT

## 5.2. Chi tiết hóa đơn (Bill Details)
Base path: `/billdetails`

### 5.2.1. Thêm món vào hóa đơn
**Endpoint**: `/`  
**Method**: POST  
**Request Body**:
```json
{
    "billID": "number",
    "foodID": "number",
    "quantity": "number"
}
```

### 5.2.2. Cập nhật số lượng món
**Endpoint**: `/{billId}/{foodId}`  
**Method**: PUT  
**Request Body**:
```json
{
    "newQuantity": "number"
}
```

### 5.2.3. Xóa món khỏi hóa đơn
**Endpoint**: `/{billId}/{foodId}`  
**Method**: DELETE

# Global Notes

## Authentication
- Tất cả API endpoints (trừ login/register) yêu cầu Bearer token
- Format: `Authorization: Bearer {token}`

## Status Codes
- 200: Success
- 201: Created
- 400: Bad Request
- 401: Unauthorized
- 404: Not Found
- 500: Internal Server Error

## Data Types
- ID fields: Integer
- Dates: yyyy-MM-dd
- DateTime: ISO 8601
- Status values:
  - Table: "EMPTY", "OCCUPIED", "RESERVED"
  - Bill: "PENDING", "PAID", "CANCELLED"

## Service Dependencies
- Account Service (8080): Authentication và user management
- User Info Service (8081): Profile và thông tin cá nhân
- Food Service (8083): Quản lý menu và món ăn
- Table Service (8084): Quản lý bàn
- Bill Service (8086): Quản lý hóa đơn và thanh toán

## Security
- CORS enabled
- Rate limiting applied
- JWT authentication required
- Token expiration handled

## Error Handling
Tất cả lỗi trả về format:
```json
{
    "status": "number",
    "message": "string",
    "timestamp": "datetime"
}
```
