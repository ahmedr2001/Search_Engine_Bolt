import { useRef, useState } from "react";
import { MdKeyboardVoice } from "react-icons/md";
import "regenerator-runtime/runtime";
import SpeechRecognition, {
	useSpeechRecognition,
	// @ts-ignore
} from "react-speech-recognition";

type Props = {
	txt: string;
	setText: React.Dispatch<React.SetStateAction<string>>;
};
export default function Voice({ txt, setText }: Props) {
	const { transcript, resetTranscript } = useSpeechRecognition();
	const [isListening, setIsListening] = useState(false);

	const handleListing = () => {
		setIsListening(true);
		SpeechRecognition.startListening({
			continuous: true,
		});
	};
	const stopHandle = () => {
		setIsListening(false);
		SpeechRecognition.stopListening();
	};

	const handelClick = () => {
		if (isListening) {
			stopHandle();
			setText(txt + transcript);
			resetTranscript();
		} else {
			resetTranscript();
			handleListing();
		}
	};

	if (!SpeechRecognition.browserSupportsSpeechRecognition()) return <></>;
	return (
		<div
			className="hover:bg-primary cursor-pointer rounded-full px-2 py-2"
			onClick={handelClick}>
			<MdKeyboardVoice />
		</div>
	);
}
