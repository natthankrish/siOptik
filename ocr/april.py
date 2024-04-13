import cv2
import numpy as np

print(np.__version__)
dictionaries = [cv2.aruco.DICT_APRILTAG_16h5, cv2.aruco.DICT_APRILTAG_25h9, cv2.aruco.DICT_APRILTAG_36h10,cv2.aruco.DICT_APRILTAG_36h11]
# Pilih dictionary ArUco yang diinginkan
image_path = "test_apriltag.png"
img = cv2.imread(image_path)

# Ubah gambar menjadi grayscale
gray_img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

for parameter in dictionaries:    
    aruco_dict = cv2.aruco.Dictionary_get(parameter)    
    # Buat detektor ArUco
    parameters = cv2.aruco.DetectorParameters_create()
    corners, ids, rejectedImgPoints = cv2.aruco.detectMarkers(gray_img, aruco_dict, parameters=parameters)
    print("Ini corners : ", corners)
    if len(corners) > 0:
        # Tandai marker di gambar
        cv2.aruco.drawDetectedMarkers(img, corners, ids)

        # Gambar kotak di sekitar setiap marker yang terdeteksi
        for corner in corners:
            pts = corner[0].astype(np.int32)
            cv2.polylines(img, [pts], True, (0, 0, 255), 5)


# Deteksi marker ArUco

# Periksa jika ada marker ArUco yang terdeteksi


output_path = "output_image2.jpg"
cv2.imwrite(output_path, img)
# print(f"Detected markers: {ids}")
print(f"Image saved as {output_path}")
