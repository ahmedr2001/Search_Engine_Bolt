import React from "react";
import resultType from "../types/resultType";

type Props = {
	res: resultType;
	paragraph: string;
};

export default function Result({ res, paragraph }: Props) {
	//console.log(res.paragraph.split(" ")[res.wIdx]);
	console.log(paragraph);
	return (
		<div className=" flex flex-col gap-1 items-start">
			<a href={res.url}>
				<h3 className=" font-bold text-lg hover:underline text-highlight">
					{res.title}
				</h3>
			</a>
			<a className=" text-neutral-500 " href={res.url}>
				{res.url}
			</a>
			{paragraph && <p className=" text-neutral-600 ">{paragraph}</p>}
		</div>
	);
}
