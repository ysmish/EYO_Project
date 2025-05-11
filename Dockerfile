FROM gcc:latest

# Create and set working directory
WORKDIR /workspace
COPY . .

# Install dependencies
RUN apt-get update && apt-get install -y \
    cmake

RUN mkdir build \
    && cd build \
    && cmake .. \
    && make

CMD ["bash"]