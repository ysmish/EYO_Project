FROM ubuntu:22.04

# Install dependencies
RUN apt-get update && \
    apt-get install -y \
    build-essential \
    cmake


# Create and set working directory
WORKDIR /workspace
COPY . .

CMD ["bash"]