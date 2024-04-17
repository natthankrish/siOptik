import numpy as np
import cv2
import os

def main(image_path):
    script_dir = os.path.dirname(os.path.abspath(__file__))

    img = image_path
    dictionaries = [cv2.aruco.DICT_APRILTAG_16h5, cv2.aruco.DICT_APRILTAG_25h9, cv2.aruco.DICT_APRILTAG_36h10,cv2.aruco.DICT_APRILTAG_36h11]

    img = cv2.imread(image_path)
    gray_img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

    for parameter in dictionaries:
        aruco_dict = cv2.aruco.Dictionary_get(parameter)
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

    output_path = os.path.join(script_dir, "apriltag.jpg")
    cv2.imwrite(output_path, img)
    return output_path

if __name__ == '__main__':
    if len(sys.argv) > 1:
        image_path = sys.argv[1]
        process_image(image_path)
    else:
        print("No image path provided")