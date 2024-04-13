import cv2
from pupil_apriltags import Detector

# Baca gambar
image_path = 'images.png'
image = cv2.imread(image_path)
# Buat detektor AprilTag
# options = apriltag.DetectorOptions(families='tag36h11')
detector = Detector(families='tag36h11',
                       nthreads=1,
                       quad_decimate=1.0,
                       quad_sigma=0.0,
                       refine_edges=1,
                       decode_sharpening=0.25,
                       debug=0)

# Konversi gambar ke grayscale
gray_image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

# Deteksi AprilTag dalam gambar
tags = detector.detect(img=gray_image,estimate_tag_pose=True)
print("Ini tags : ", tags)
# Tampilkan hasil
for tag in tags:
    print(f"Tag ID: {tag.tag_id}")
    print(f"Tag Center: {tag.center}")
    print(f"Tag Corners: {tag.corners}")

for tag in tags:
    # Gambar kotak sekitar AprilTag
    (ptA, ptB, ptC, ptD) = tag.corners
    ptA = (int(ptA[0]), int(ptA[1]))
    ptB = (int(ptB[0]), int(ptB[1]))
    ptC = (int(ptC[0]), int(ptC[1]))
    ptD = (int(ptD[0]), int(ptD[1]))
    
    cv2.line(image, ptA, ptB, (0, 255, 0), 2)
    cv2.line(image, ptB, ptC, (0, 255, 0), 2)
    cv2.line(image, ptC, ptD, (0, 255, 0), 2)
    cv2.line(image, ptD, ptA, (0, 255, 0), 2)

    # Tampilkan gambar
    cv2.imshow("Image", image)
    cv2.waitKey(0)
    cv2.destroyAllWindows()