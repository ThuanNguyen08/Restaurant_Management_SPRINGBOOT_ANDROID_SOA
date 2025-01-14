# Restaurant Management System API Documentation
Version: 1.0  
Last Updated: January 14, 2025

## Service Ports
- Account Service: 8080
- User Information Service: 8081
- Food Service: 8083
- Table Service: 8084
- Bill Service: 8086

# 1. Account Service (Port 8080)
Base URL: `http://localhost:8080/api/v1`

## 1.1. Đăng nhập (Login)
**Endpoint**: `/login`  
**Method**: POST  
**Description**: Đăng nhập và lấy JWT token

**Request Headers**: 
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
```json
"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Error Response**:
- Status: 401 UNAUTHORIZED
```json
{
    "message": "Sai username hoặc password."
}
```

## 1.2. Đăng ký (Register)
**Endpoint**: `/register`  
**Method**: POST  
**Description**: Đăng ký tài khoản mới

**Request Headers**:
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
```json
{
    "message": "Đăng ký thành công"
}
```

**Error Response**:
- Status: 400 BAD REQUEST
```json
{
    "message": "Tài khoản đã tồn tại"
}
```

## 1.3. Lấy ID tài khoản (Get Account ID)
**Endpoint**: `/get-accountID`  
**Method**: GET  
**Description**: Lấy ID của tài khoản từ token

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
```json
{
    "accountId": 123
}
```

**Error Response**:
- Status: 401 UNAUTHORIZED
```json
{
    "message": "Token không hợp lệ"
}
```

## 1.4. Lấy loại tài khoản (Get Account Type)
**Endpoint**: `/auth/get-accountType`  
**Method**: GET  
**Description**: Lấy loại tài khoản của người dùng

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
```json
{
    "accountType": "ADMIN"
}
```

**Error Response**:
- Status: 401 UNAUTHORIZED
```json
{
    "message": "Token không hợp lệ"
}
```

## 1.5. Kiểm tra xác thực (Verify Authentication)
**Endpoint**: `/auth`  
**Method**: GET  
**Description**: Kiểm tra tính hợp lệ của token

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
```json
true
```

**Error Response**:
- Status: 401 UNAUTHORIZED
```json
{
    "message": "Token không hợp lệ"
}
```

# 2. User Information Service (Port 8081)
Base URL: `http://localhost:8081/api/v1/infoUser`

## 2.1. Lấy tất cả thông tin người dùng
**Endpoint**: `/`  
**Method**: GET  
**Description**: Lấy danh sách thông tin của tất cả người dùng

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
```json
[
    {
        "userInfoId": 1,
        "accountId": 123,
        "fullName": "Nguyễn Văn A",
        "sex": "Nam",
        "email": "example@email.com",
        "phoneNumber": "0123456789"
    }
]
```

**Error Response**:
- Status: 401 UNAUTHORIZED
```json
{
    "message": "Authentication failed"
}
```

## 2.2. Lấy ID người dùng
**Endpoint**: `/getId`  
**Method**: GET  
**Description**: Lấy userInfoId từ token

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
```json
{
    "userInfoId": 1
}
```

**Error Response**:
- Status: 401 UNAUTHORIZED
```json
{
    "message": "Có lỗi xảy ra rồi"
}
```

## 2.3. Lấy thông tin người dùng
**Endpoint**: `/info`  
**Method**: GET  
**Description**: Lấy thông tin chi tiết của người dùng

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
```json
{
    "userInfoId": 1,
    "accountId": 123,
    "fullName": "Nguyễn Văn A",
    "sex": "Nam",
    "email": "example@email.com",
    "phoneNumber": "0123456789"
}
```

**Error Response**:
- Status: 401 UNAUTHORIZED
```json
{
    "message": "Có lỗi xảy ra rồi"
}
```

## 2.4. Thêm/Cập nhật thông tin người dùng
**Endpoint**: `/add`  
**Method**: POST  
**Description**: Thêm mới hoặc cập nhật thông tin người dùng

**Request Headers**:
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

**Success Response**:
- Status: 200 OK
```json
{
    "message": "Thêm thành công"
}
```

**Error Response**:
- Status: 400 BAD REQUEST
```json
{
    "message": "Có lỗi xảy ra khi thêm"
}
```

# 3. Food Management Service (Port 8083)
Base URL: `http://localhost:8083/api/v1`

## 3.1. Danh Mục Món Ăn (Food Categories)
Base path: `/dmFood`

### 3.1.1. Lấy tất cả danh mục
**Endpoint**: `/`  
**Method**: GET  
**Description**: Lấy danh sách tất cả danh mục món ăn

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
```json
[
    {
        "dmFoodId": 1,
        "categoryName": "Món chính"
    },
    {
        "dmFoodId": 2,
        "categoryName": "Món tráng miệng"
    }
]
```

### 3.1.2. Lấy danh mục theo ID
**Endpoint**: `/{id}`  
**Method**: GET  
**Description**: Lấy thông tin danh mục theo ID

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
```json
{
    "dmFoodId": 1,
    "categoryName": "Món chính"
}
```

**Error Response**:
- Status: 200 OK
```json
{
    "message": "Id không tồn tại"
}
```

### 3.1.3. Thêm danh mục mới
**Endpoint**: `/add`  
**Method**: POST  
**Description**: Thêm một danh mục món ăn mới

**Request Headers**:
- Authorization: Bearer {token}
- Content-Type: application/json

**Request Body**:
```json
{
    "categoryName": "string"
}
```

**Success Response**:
- Status: 200 OK
```json
{
    "message": "Thêm mới danh mục thành công"
}
```

**Error Response**:
- Status: 200 OK
```json
{
    "message": "tên danh mục đã tồn tại"
}
```

### 3.1.4. Xóa danh mục
**Endpoint**: `/{id}`  
**Method**: DELETE  
**Description**: Xóa một danh mục và tất cả món ăn trong danh mục

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
```json
{
    "message": "Xóa thành công"
}
```

**Error Response**:
- Status: 200 OK
```json
{
    "message": "Xóa thất bại"
}
```

## 3.2. Món Ăn (Food Items)
Base path: `/food`

### 3.2.1. Lấy tất cả món ăn
**Endpoint**: `/`  
**Method**: GET  
**Description**: Lấy danh sách tất cả các món ăn

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
```json
[
    {
        "foodID": 1,
        "foodName": "Phở bò",
        "dmFoodID": 1,
        "price": "50000",
        "avtFood": "byte[]"
    }
]
```

### 3.2.2. Lấy món ăn theo ID
**Endpoint**: `/{id}`  
**Method**: GET  
**Description**: Lấy thông tin chi tiết của một món ăn

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
```json
{
    "foodID": 1,
    "foodName": "Phở bò",
    "dmFoodID": 1,
    "price": "50000",
    "avtFood": "byte[]"
}
```

### 3.2.3. Lấy món ăn theo danh mục
**Endpoint**: `/category/{dmFoodID}`  
**Method**: GET  
**Description**: Lấy tất cả món ăn trong một danh mục

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
```json
[
    {
        "foodID": 1,
        "foodName": "Phở bò",
        "dmFoodID": 1,
        "price": "50000",
        "avtFood": "byte[]"
    }
]
```

**Error Response**:
- Status: 404 NOT_FOUND
```json
{
    "message": "Không tìm thấy món ăn nào trong danh mục này."
}
```

### 3.2.4. Thêm món ăn mới
**Endpoint**: `/add`  
**Method**: POST  
**Description**: Thêm món ăn mới vào menu

**Request Headers**:
- Authorization: Bearer {token}
- Content-Type: application/json

**Request Body**:
```json
{
    "foodName": "string",
    "dmFoodID": "number",
    "price": "string",
    "avtFood": "byte[]"
}
```

**Success Response**:
- Status: 200 OK
```json
{
    "message": "Thêm thành công món ăn: {foodName}"
}
```

**Error Response**:
- Status: 400 BAD REQUEST
```json
{
    "message": "Danh mục chưa tồn tại"
}
```

### 3.2.5. Cập nhật món ăn
**Endpoint**: `/update/{foodId}`  
**Method**: PUT  
**Description**: Cập nhật thông tin món ăn

**Request Headers**:
- Authorization: Bearer {token}
- Content-Type: application/json

**Request Body**:
```json
{
    "foodName": "string",
    "dmFoodID": "number",
    "price": "string"
}
```

**Success Response**:
- Status: 200 OK
```json
{
    "message": "Cập nhật món ăn thành công: {foodName}"
}
```

### 3.2.6. Xóa món ăn
**Endpoint**: `/{foodID}`  
**Method**: DELETE  
**Description**: Xóa một món ăn khỏi menu

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
```json
{
    "message": "Xóa món ăn thành công với ID: {foodID}"
}
```

# 4. Table Management Service (Port 8084)
Base URL: `http://localhost:8084/api/v1/tables`

## 4.1. Tạo bàn mới
**Endpoint**: `/`  
**Method**: POST  
**Description**: Tạo một bàn mới trong hệ thống

**Request Headers**:
- Authorization: Bearer {token}
- Content-Type: application/json

**Request Body**:
```json
{
    "tableName": "string",
    "status": "string"
}
```

**Success Response**:
- Status: 201 CREATED
```json
{
    "tableID": 1,
    "tableName": "Bàn 1",
    "status": "EMPTY"
}
```

**Error Response**:
- Status: 400 BAD REQUEST
```json
{
    "message": "Tên bàn đã tồn tại: {tableName}"
}
```

## 4.2. Lấy danh sách bàn
**Endpoint**: `/`  
**Method**: GET  
**Description**: Lấy thông tin tất cả các bàn

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
```json
[
    {
        "tableID": 1,
        "tableName": "Bàn 1",
        "status": "EMPTY"
    }
]
```

## 4.3. Lấy thông tin bàn theo ID
**Endpoint**: `/{id}`  
**Method**: GET  
**Description**: Lấy thông tin chi tiết của một bàn

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200
```json
{
    "tableID": 1,
    "tableName": "Bàn 1",
    "status": "EMPTY"
}
```

**Error Response**:
- Status: 404 NOT_FOUND
```json
{
    "message": "Không tìm thấy bàn với id: {id}"
}
```

## 4.4. Cập nhật trạng thái bàn
**Endpoint**: `/{id}`  
**Method**: PUT  
**Description**: Cập nhật trạng thái của một bàn cụ thể

**Request Headers**:
- Authorization: Bearer {token}
- Content-Type: text/plain

**Request Body**: 
- String (EMPTY/OCCUPIED/RESERVED)

**Success Response**:
- Status: 200 OK
```json
{
    "tableID": 1,
    "tableName": "Bàn 1",
    "status": "OCCUPIED"
}
```

**Error Response**:
- Status: 404 NOT_FOUND
```json
{
    "message": "Không tìm thấy bàn với id: {id}"
}
```

## 4.5. Xóa bàn
**Endpoint**: `/{id}`  
**Method**: DELETE  
**Description**: Xóa một bàn khỏi hệ thống

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 204 NO_CONTENT

**Error Response**:
- Status: 400 BAD REQUEST
```json
{
    "message": "Không thể xóa bàn đang có người ngồi"
}
```

# 5. Bill Management Service (Port 8086)
Base URL: `http://localhost:8086/api/v1`

## 5.1. Quản lý hóa đơn (Bills)
Base path: `/bills`

### 5.1.1. Tạo hóa đơn mới
**Endpoint**: `/`  
**Method**: POST  
**Description**: Tạo một hóa đơn mới

**Request Headers**:
- Authorization: Bearer {token}
- Content-Type: application/json

**Request Body**:
```json
{
    "tableID": "number"
}
```

**Success Response**:
- Status: 201 CREATED
```json
{
    "billID": 1,
    "tableID": 1,
    "userInfoID": 1,
    "billDate": "2025-01-14T10:30:00",
    "totalAmount": 0,
    "status": "PENDING"
}
```

**Error Response**:
- Status: 400 BAD REQUEST
```json
{
    "message": "Bàn này đã có hóa đơn đang xử lý"
}
```

### 5.1.2. Lấy hóa đơn đã thanh toán theo ngày
**Endpoint**: `/paid-bills`  
**Method**: GET  
**Description**: Lấy danh sách hóa đơn đã thanh toán theo ngày cụ thể

**Request Parameters**:
- date: yyyy-MM-dd (required)

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
```json
[
    {
        "billID": 1,
        "tableID": 1,
        "userInfoID": 1,
        "billDate": "2025-01-14T10:30:00",
        "totalAmount": 150000,
        "status": "PAID"
    }
]
```

**Error Response**:
- Status: 204 NO_CONTENT
```json
{
    "message": "Không có hóa đơn đã thanh toán cho ngày 2025-01-14"
}
```

### 5.1.3. Lấy hóa đơn theo ID
**Endpoint**: `/{id}`  
**Method**: GET  
**Description**: Lấy thông tin chi tiết của một hóa đơn

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
```json
{
    "billID": 1,
    "tableID": 1,
    "userInfoID": 1,
    "billDate": "2025-01-14T10:30:00",
    "totalAmount": 150000,
    "status": "PENDING"
}
```

### 5.1.4. Lấy hóa đơn theo bàn
**Endpoint**: `/table/{tableId}`  
**Method**: GET  
**Description**: Lấy hóa đơn hiện tại của một bàn

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
```json
{
    "billID": 1,
    "tableID": 1,
    "userInfoID": 1,
    "billDate": "2025-01-14T10:30:00",
    "totalAmount": 150000,
    "status": "PENDING"
}
```

### 5.1.5. Cập nhật tổng tiền
**Endpoint**: `/total/{id}`  
**Method**: PUT  
**Description**: Cập nhật tổng tiền của hóa đơn

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
```json
{
    "billID": 1,
    "totalAmount": 150000
}
```

### 5.1.6. Thanh toán hóa đơn
**Endpoint**: `/pay/{id}`  
**Method**: PUT  
**Description**: Thanh toán một hóa đơn

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
```json
{
    "billID": 1,
    "status": "PAID",
    "totalAmount": 150000
}
```

**Error Response**:
- Status: 400 BAD REQUEST
```json
{
    "message": "Hóa đơn đã được thanh toán hoặc đã hủy"
}
```

### 5.1.7. Hủy hóa đơn
**Endpoint**: `/{id}/cancel`  
**Method**: PUT  
**Description**: Hủy một hóa đơn

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
```json
{
    "billID": 1,
    "status": "CANCELLED"
}
```

## 5.2. Quản lý chi tiết hóa đơn (Bill Details)
Base path: `/billdetails`

### 5.2.1. Thêm món vào hóa đơn
**Endpoint**: `/`  
**Method**: POST  
**Description**: Thêm một món ăn vào hóa đơn

**Request Headers**:
- Authorization: Bearer {token}
- Content-Type: application/json

**Request Body**:
```json
{
    "billID": "number",
    "foodID": "number",
    "quantity": "number"
}
```

**Success Response**:
- Status: 201 CREATED
```json
{
    "billDetailID": 1,
    "billID": 1,
    "foodID": 1,
    "quantity": 2,
    "price": 50000
}
```

### 5.2.2. Cập nhật số lượng món
**Endpoint**: `/{billId}/{foodId}`  
**Method**: PUT  
**Description**: Cập nhật số lượng của một món trong hóa đơn

**Request Headers**:
- Authorization: Bearer {token}
- Content-Type: application/json

**Request Body**:
```json
{
    "newQuantity": "number"
}
```

**Success Response**:
- Status: 200 OK
```json
{
    "billDetailID": 1,
    "quantity": 3
}
```

### 5.2.3. Xóa món khỏi hóa đơn
**Endpoint**: `/{billId}/{foodId}`  
**Method**: DELETE  
**Description**: Xóa một món khỏi hóa đơn

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 204 NO_CONTENT

### 5.2.4. Lấy tất cả món theo billId
**Endpoint**: `/bill/{billId}`  
**Method**: GET  
**Description**: Lấy danh sách tất cả món ăn trong một hóa đơn

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
```json
[
    {
        "billDetailID": 1,
        "billID": 1,
        "foodID": 1,
        "quantity": 2,
        "price": 50000
    }
]
```

### 5.2.5. Kiểm tra món ăn trong hóa đơn
**Endpoint**: `/bill/{billId}/{foodId}`  
**Method**: GET  
**Description**: Kiểm tra một món ăn đã có trong hóa đơn chưa

**Request Headers**:
- Authorization: Bearer {token}

**Success Response**:
- Status: 200 OK
```json
{
    "message": "Món ăn đã tồn tại"
}
```

**Error Response**:
- Status: 404 NOT_FOUND
```json
{
    "message": "Món ăn chưa tồn tại"
}
```

# 6. Notes and Conventions

## Authentication
- Tất cả API endpoints (trừ login/register) yêu cầu Bearer token
- Token format: `Authorization: Bearer {token}`
- Token expiration: 24 giờ

## Status Codes
- 200: Success
- 201: Created
- 204: No Content
- 400: Bad Request
- 401: Unauthorized
- 404: Not Found
- 500: Internal Server Error

## Data Types and Formats
- ID fields: Integer
- Dates: yyyy-MM-dd
- DateTime: ISO 8601 (yyyy-MM-ddTHH:mm:ss)
- Currency: VND (Integer)
- Images: byte[] (LONGBLOB)

## Trạng thái trong hệ thống
1. Trạng thái bàn:
   - EMPTY: Bàn trống
   - OCCUPIED: Đang có khách
   - RESERVED: Đã đặt trước

2. Trạng thái hóa đơn:
   - PENDING: Đang xử lý
   - PAID: Đã thanh toán
   - CANCELLED: Đã hủy

## Error Handling
Mọi lỗi đều trả về format:
```json
{
    "status": "number",
    "message": "string",
    "timestamp": "datetime"
}
```

## Dependencies giữa các Service
1. Account Service (8080):
   - Xác thực và phân quyền cho tất cả service khác

2. User Info Service (8081):
   - Phụ thuộc Account Service để lấy thông tin người dùng

3. Food Service (8083):
   - Độc lập với các service khác
   - Được gọi bởi Bill Service để lấy thông tin món ăn

4. Table Service (8084):
   - Được gọi bởi Bill Service để cập nhật trạng thái bàn

5. Bill Service (8086):
   - Phụ thuộc Table Service để quản lý trạng thái bàn
   - Phụ thuộc Food Service để lấy thông tin và giá món ăn
   - Phụ thuộc Account Service để lấy thông tin người tạo hóa đơn
