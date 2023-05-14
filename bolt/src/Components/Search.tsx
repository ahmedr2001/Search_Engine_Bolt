import React, { useEffect } from "react";
import useSound from "use-sound";
import { BsFillKeyboardFill } from "react-icons/bs";
import bolt from "../assets/Vector.png";
import keyPressSound from "../assets/keyPress.mp3";
import Voice from "./Voice";
import useSearchParamQuery from "../hooks/useSearchParamQuery";

type Props = {
	isSearching: boolean;
	setIsSearching: React.Dispatch<React.SetStateAction<boolean>>;

	txt: string;
	setText: React.Dispatch<React.SetStateAction<string>>;
	setIsFocused: React.Dispatch<React.SetStateAction<boolean>>;
	inputRef: React.RefObject<HTMLInputElement>;
};

export default function Search({
	txt,
	setText,
	setIsFocused,
	inputRef,
}: Props) {
	const [query, isSeeingResults, setQuery] = useSearchParamQuery();

	const [play] = useSound(keyPressSound, { volume: 0.7 });

	const handelInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		setText(e.target.value);
		play();
	};
	const handelSearchQuery = (e: React.FormEvent<HTMLFormElement>) => {
		e.preventDefault();
		setQuery(txt);
	};

	useEffect(() => {
		if (isSeeingResults()) setText(query);
	}, []);

	return (
		<div
			className={`transition-all mx-auto flex flex-row flex-grow justify-between items-center py-1 px-6   border-2 rounded-full border-highlight bg-overlay text-highlight ${
				isSeeingResults() ? "w-11/12" : "w-7/12"
			}`}>
			<img src={bolt} alt="bolt" width={15} />
			<form onSubmit={handelSearchQuery} className=" flex-grow px-3">
				<input
					ref={inputRef}
					type="text"
					className=" w-full border-0 outline-none bg-transparent"
					onChange={handelInputChange}
					onFocus={() => setIsFocused(true)}
					onBlur={() => setIsFocused(false)}
					value={txt}
				/>
			</form>
			<div className="flex flex-row gap-1 text-2xl ">
				<Voice txt={txt} setText={setText} />
				<KeyBoard />
			</div>
		</div>
	);
}

const KeyBoard = () => {
	return (
		<div className="hover:bg-primary cursor-pointer rounded-full px-2 py-2">
			<BsFillKeyboardFill />
		</div>
	);
};
