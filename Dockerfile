FROM java:8

RUN apt-get -qq update &&  apt-get -qq -y install build-essential

RUN apt-get install g++ # or clang++ (presumably)

RUN apt-get install -qq -y autoconf automake libtool

RUN apt-get install autoconf-archive

RUN apt-get install pkg-config

RUN apt-get install -qq -y libpng12-dev

RUN apt-get update && apt-get install libjpeg62-turbo-dev

RUN apt-get install -qq -y libtiff5-dev

RUN apt-get install zlib1g-dev

RUN wget http://www.leptonica.org/source/leptonica-1.74.4.tar.gz

RUN export PATH=${PATH}:/c/MinGW/bin && gcc --version

RUN tar xvf leptonica-1.74.4.tar.gz && cd leptonica-1.74.4 && ./configure && make && make install

RUN apt-get -qq -y install libtool

RUN apt-get install autotools-dev

RUN apt-get -qq -y install automake

RUN apt-get install autoconf-archive

RUN apt-get install pkg-config 

RUN pkg-config --cflags --libs lept

RUN git clone https://github.com/tesseract-ocr/tesseract.git tesseract-ocr

RUN ls

RUN cd tesseract-ocr && ./autogen.sh && ./configure --enable-debug && LDFLAGS="-L/usr/local/lib" CFLAGS="-I/usr/local/include" make && make install && ldconfig

RUN tesseract --version

RUN wget https://github.com/tesseract-ocr/tessdata/raw/master/eng.traineddata && mv -v eng.traineddata /usr/local/share/tessdata/

RUN wget --no-check-certificate https://www.python.org/ftp/python/3.6.4/Python-3.6.4.tgz

RUN tar -xzf Python-3.6.4.tgz

RUN cd Python-3.6.4 && ./configure && make && make install

RUN wget https://www.imagemagick.org/download/ImageMagick.tar.gz

RUN tar xvzf ImageMagick.tar.gz

RUN ls

RUN cd ImageMagick-7.0.7-19 && ./configure && make && make install && ldconfig /usr/local/lib 

RUN apt-get -qq -y install python-numpy

RUN apt-get -qq -y install python-matplotlib

RUN apt-get -qq -y install python-scipy

RUN apt-get -qq -y install build-essential cython

RUN apt-get update

RUN apt-get -qq -y install python-skimage

VOLUME /tmp

EXPOSE 9099

ADD build/libs/ocr-tika.jar app.jar

RUN bash -c 'touch /app.jar'

ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar
