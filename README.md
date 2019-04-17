## Example of sending gRPC requests from JobService

To make things work, put `.proto` files into app/src/main/api directory and make necessary changes in `RequestHelper::retryGrpcRequest` and `App::GRPC_PORT/GRPC_HOST`.
All files in io/grpc folder are copied from 1.20.0 to capture additional logs.